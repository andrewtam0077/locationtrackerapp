//Utiilized Rahul Pandey code from his Android Google Maps Tutorial as a base

package com.WorkSummer2020.projectdembeone

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.WorkSummer2020.projectdembeone.models.Satellite
import com.WorkSummer2020.projectdembeone.models.UserMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

private const val TAG = "MainActivity"
private const val FILENAME = "RandomFile"
const val EXTRA_USER_MAP = "EXTRA_USER_MAP"
const val EXTRA_MAP_TITLE = "EXTRA_MAP_TITLE"
private const val REQUEST_CODE = 1234
class MainActivity : AppCompatActivity() {

    private lateinit var userMaps : MutableList<UserMap>
    private lateinit var mapAdapter: MapsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userMaps = generateSampleData().toMutableList()
        //deserializeUserMaps(this).toMutableList()

        // Set layout manager on the recyler view
        rvSats.layoutManager = LinearLayoutManager(this)
        // Set adapter on the recycler view
        mapAdapter = MapsAdapter(this, userMaps, object: MapsAdapter.OnClickListener{
            override fun onItemClick(position: Int) {
                Log.i(TAG, "onItemClick $position")
                // When user taps on view in RV, navigate to new activity
                //intent is what launches us to another page in this case we are using an explicit intent to get us to maps
                val intent = Intent(this@MainActivity, DisplayMapActivity::class.java)
                //this adds additional data to the other page that we are going to
                //we are going to serialize the usermaps so that we can pass this using the putExtra method 
                intent.putExtra(EXTRA_USER_MAP, userMaps[position]) // title, and then data structure, in place of userMaps we can use complex data structures
                startActivity(intent) // starts the activity through the intent
            }
        })
        rvSats.adapter = mapAdapter

        fabCreateMap.setOnClickListener {
            Log.i(TAG, "Tap on FAB")
//            val intent = Intent(this@MainActivity, CreateMapActivity::class.java)
//            intent.putExtra(EXTRA_MAP_TITLE, "new path name")
//            startActivityForResult(intent, REQUEST_CODE)
            showAlertDialog()
        }
    }
    /**
     * This creates a dialog window to confirm if the user would like to make a new path listing
     */
    private fun showAlertDialog() {
        val mapFormView = LayoutInflater.from(this).inflate(R.layout.dialogue_create_map, null)
        val dialog =
            AlertDialog.Builder(this)
                .setTitle("Map Title")
                .setView(mapFormView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", null)
                .show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            //error validation
            val title = mapFormView.findViewById<EditText>(R.id.etTitle).text.toString()
            if(title.trim().isEmpty()) {
                Toast.makeText(this, "Map must have non-empty title", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            //navigate to create map activity
            val intent = Intent(this@MainActivity, CreateMapActivity::class.java)
            intent.putExtra(EXTRA_MAP_TITLE, title)
            startActivityForResult(intent, REQUEST_CODE)
            dialog.dismiss()
        }
    }

    /**
     *
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //get new map data from the data
            val userMap = data?.getSerializableExtra(EXTRA_USER_MAP) as UserMap
            Log.i(TAG, "onActivityResult with new map title ${userMap.title}")
            userMaps.add(userMap)
            mapAdapter.notifyItemInserted(userMaps.size -1)
            serializeUserMaps(this, userMaps)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * Method for getting data file... currently only supporting local files
     */
    private fun getDataFile(context: Context) : File {
        Log.i(TAG, "Getting file from directory ${context.filesDir}")
        return File(context.filesDir, FILENAME)
    }

    /**
     * Deserialization: reading from a file
     */
    private fun deserializeUserMaps(context: Context) : List<UserMap> {
        Log.i(TAG, "deserializeUserMaps")
        val dataFile = getDataFile(context)
        if (!dataFile.exists()) {
            Log.i(TAG, "Data file does not exist.")
            return emptyList()
        }
        ObjectInputStream(FileInputStream(dataFile)).use { return it.readObject() as List<UserMap>}
    }

    /**
     * Serialization: writing to a file
     */
    private fun serializeUserMaps(context: Context, userMaps: List<UserMap>) {
        Log.i(TAG, "serializeUserMaps")
        // we already serialized a usermap object
        ObjectOutputStream(FileOutputStream(getDataFile(context))).use { it.writeObject(userMaps)}
    }


    private fun generateSampleData() : List<UserMap> {
        return listOf(
            UserMap(
                "path1",
                listOf(
                    //potential to add further "paths through marker placement"
                    Satellite("asia1", "indonesia", 6.2088, 106.8456), //indonesia as example
                    Satellite("asia2", "thailand", 13.726316, 100.591507) // thailand as example

                )
            ),
            UserMap(
                "path2",
                listOf(
                    Satellite("asia3", "korea", 35.9078, 127.7669), // thailand as example
                    Satellite("asia4", "japan", 36.2048, 138.2529) // thailand as example

                )
            ),
            UserMap(
                "path3",
                listOf(
                    Satellite("one", "indonesia", 6.2088, 106.8456),
                    Satellite("two", "thailand", 7.0, 107.0),
                    Satellite("three", "indonesia", 8.0, 108.06),
                    Satellite("four", "thailand", 9.0, 109.07),
                    Satellite("five", "indonesia", 10.0, 110.8456),
                    Satellite("six", "thailand", 11.0, 111.8456),
                    Satellite("seven", "indonesia", 12.0, 112.8456),
                    Satellite("eight", "thailand", 13.726316, 113.8456)
                )
            )

        )
    }

}