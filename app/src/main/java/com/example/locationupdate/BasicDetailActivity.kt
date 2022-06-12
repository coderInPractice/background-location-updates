package com.example.locationupdate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.locationupdate.utils.AppsConstant.DATA_PRESENT
import com.example.locationupdate.utils.AppsConstant.MOBILE_NUMBER
import com.example.locationupdate.utils.AppsConstant.MY_SHARED_PREF
import com.example.locationupdate.utils.AppsConstant.NAME

class BasicDetailActivity : AppCompatActivity() {

    companion object {

    }

    private lateinit var etName:EditText
    private lateinit var etMobile:EditText
    private lateinit var saveBtn:Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic_detail)

        etName = findViewById(R.id.et_name)
        etMobile = findViewById(R.id.et_mobile)
        saveBtn = findViewById(R.id.save_btn)

        val sharedPreferences = getSharedPreferences(MY_SHARED_PREF, MODE_PRIVATE)
        val edit = sharedPreferences.edit()

        saveBtn.setOnClickListener {
            if (etName.text.toString().isNotEmpty() && etMobile.text.toString().isNotEmpty()) {
                edit.putString(NAME, etName.text.toString())
                edit.putString(MOBILE_NUMBER, etMobile.text.toString())
                edit.putBoolean(DATA_PRESENT, true)
                edit.apply()
                Intent(this, MainActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            } else {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_LONG).show()
            }
        }
    }
}