package com.example.contacts

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contacts.contact.ContactAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeRefreshLayout.setOnRefreshListener {
            checkPermissionAndHandleResult()
            swipeRefreshLayout.isRefreshing = false
        }

        goUpButton.setOnClickListener {
            recyclerView.scrollToPosition(0)
        }

        checkPermissionAndHandleResult()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            CONTACTS_REQUEST_PERMISSION_ID -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showContacts()
                } else {
                    Toast.makeText(this@MainActivity, getString(R.string.no_permission_message), Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun checkPermissionAndHandleResult(): Unit =
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), CONTACTS_REQUEST_PERMISSION_ID)
        } else {
            showContacts()
        }

    private fun showContacts() {
        val locale = resources.configuration.locales[0]
        val contacts = fetchAllContacts().sortedBy { it.name.toLowerCase(locale) }
        val viewManager = LinearLayoutManager(this)

        recyclerView.apply {
            layoutManager = viewManager
            adapter = ContactAdapter(contacts) {
                dialPhoneNumber(it.phone)
            }
        }
    }

    companion object {
        private const val CONTACTS_REQUEST_PERMISSION_ID: Int = 13
    }
}
