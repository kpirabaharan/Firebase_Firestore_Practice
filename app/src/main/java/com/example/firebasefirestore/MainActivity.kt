package com.example.firebasefirestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {

    private val personCollectionRef = Firebase.firestore.collection("persons")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnUploadData.setOnClickListener {
            val firstName = etFirstName.text.toString()
            val lastName = etLastName.text.toString()
            val age = etAge.text.toString().toInt()
            val person = Person(firstName,lastName,age)
            savePerson(person)
        }
        btnRetrieveData.setOnClickListener {
            retrievePersons()
        }
    }

    private fun retrievePersons() = CoroutineScope(Dispatchers.IO).launch{
        try{
            val querySnapshot = personCollectionRef.get().await() // await needs to be called after every task in coroutine
            // get function retrieves all documents in collection of personCollectionRef
            val sb = StringBuilder()
            // to append all person strings
            for(document in querySnapshot.documents){
                val person = document.toObject<Person>()
                // Needs a default constructor in case null values were entered when creating person
                sb.append("$person\n")
                // convert data from document to person class
            }
            withContext(Dispatchers.Main){
                tvPersons.text = sb.toString()
            }
        }catch(e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun savePerson(person: Person) = CoroutineScope(Dispatchers.IO).launch {
        try {
            personCollectionRef.add(person).await()
            // adds person to personCollectionRef
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity, "Successfully saved data.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}