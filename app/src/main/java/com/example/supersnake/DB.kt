package com.example.supersnake

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    private lateinit var highScoreDisplay: TextView
    private lateinit var generator: Button
    private lateinit var dbHelper: MyDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.main_activity)

        //highScoreDisplay = findViewById(R.id.display)
        //generator = findViewById(R.id.rdnGenerator)
        dbHelper = MyDBHelper(this)

        updateDisplay()

        generator.setOnClickListener {
            val randomNumber = Random.nextInt(1, 101)
            if(randomNumber > dbHelper.getValue()){
                dbHelper.saveValue(randomNumber)
                updateDisplay()
            }
        }
    }

    private fun updateDisplay() {
        var displayText = highScoreDisplay.text.toString()
        displayText = displayText.replaceFirst("\\d+$".toRegex(), "")
        val displayTextNew = displayText + dbHelper.getValue()
        highScoreDisplay.text = displayTextNew
    }

}

class MyDBHelper(context: Context) : SQLiteOpenHelper(context,"mydb",null,1){

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE myTable (id INTEGER PRIMARY KEY, value INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun saveValue(value: Int) {
        if(value > getValue()){
            val db = writableDatabase
            db.execSQL("INSERT OR REPLACE INTO myTable (id,value) VALUES (1,$value)")
            db.close()
        }
    }


    @SuppressLint("Range")
    fun printValues(){
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM myTable", null)
        println(cursor.count)
        if (cursor.moveToFirst()) {
            // Iterate over the rows
            do {
                // Retrieve the values for each column
                val columnValue = cursor.getInt(cursor.getColumnIndex("value"))
                // Print the values
                println("Value: $columnValue")
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    fun deleteValues(){
        val db = writableDatabase
        val tableName = "myTable"

        val rowsDeleted = db.delete(tableName, null, null)

        if (rowsDeleted > 0) {
            println("All values deleted successfully.")
            println(rowsDeleted)
        } else {
            println("No values found to delete.")
        }
        db.close()

    }

    @SuppressLint("Range")
    fun getValue(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM myTable", null)
        if(cursor.count == 0)
            return 0
        cursor.moveToFirst()
        val value = cursor.getInt(cursor.getColumnIndex("value"))
        cursor.close()
        db.close()
        return value
    }
}

