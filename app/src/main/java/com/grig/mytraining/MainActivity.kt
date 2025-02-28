package com.grig.mytraining

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.grig.mytraining.databinding.ActivityMainBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    private var navController: NavController? = null
    private var currentItem = R.id.navigation_home;
    var currentDate: LocalDate = LocalDate.now()

    @SuppressLint("NonConstantResourceId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("lastSaveDate", MODE_PRIVATE)
//        println("2023-04-13" >= "2023-04-12")
//        println(currentDate.plusDays(7).toString())
//        prefs.edit().putString("lastSaveDate", currentDate.minusDays(15).toString()).apply()
        println(prefs.getString("lastSaveDate", ""))
        if (prefs.getString("lastSaveDate", "").isNullOrBlank() || prefs.getString("lastSaveDate", "").toString() <= currentDate.minusDays(7).toString()) {
            val res : Boolean = exportDB()
            if (res) {
                prefs.edit().putString("lastSaveDate", currentDate.toString()).apply()
            }
            println(res)
        }
//        prefs.edit().putString("lastSaveDate", currentDate.toString()).apply();
        // выставляем тренировочные дни при создании приложения
        MyHelper.MyDBHelper.TrainingDaysFromDB.updateTrainingDays()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        navView.bringToFront()
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navController = findNavController(this, R.id.nav_host_fragment_activity_main)
        setupWithNavController(binding.navView, navController!!)
        navView.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.navigation_home -> if (currentItem != R.id.navigation_home) switchToHome()
                R.id.navigationChronology -> if (currentItem != R.id.navigationChronology) switchToChronology()
                R.id.navigationWeight -> if (currentItem != R.id.navigationWeight) switchToWeight()
            }
            currentItem = item.itemId
            false
        }
    }

    private fun exportDB(): Boolean {
        val backupFolder = File(Environment.getExternalStorageDirectory(), "MyTrainingBackupDB")
        if (PermissionUtils.hasPermissions(applicationContext)) {
            if (backupFolder.canWrite()) {
                val currentDB = applicationContext.getDatabasePath("trainingDB").absoluteFile
                val copyDB = File(backupFolder, "Program" + "trainingDB" + "Backup")
                if (currentDB.exists()) {
                    val inputStream: FileChannel
                    val outputStream: FileChannel
                    return try {
                        inputStream = FileInputStream(currentDB).channel
                        outputStream = FileOutputStream(copyDB).channel
                        outputStream.transferFrom(inputStream, 0, inputStream.size())
                        //                            FileUtils.copy(new FileInputStream(currentDB), new FileOutputStream(copyDB));
                        Toast.makeText(this, "База данных сохранена!", Toast.LENGTH_SHORT).show()
                        true
                    } catch (ignored: IOException) {
                        false
                    }
                } else {
                    Toast.makeText(this, "База данных не существует!", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (backupFolder.mkdir()) {
                    Toast.makeText(this, "Создание каталога...", Toast.LENGTH_SHORT).show()
                    exportDB()
                } else Toast.makeText(this, "Невозможно создать каталог!", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            PermissionUtils.requestPermissions(this@MainActivity, 101)
        }
        return false
    }

    private fun switchToHome() = navController!!.navigate(R.id.navigation_home)

    private fun switchToChronology() {
        MyHelper.MyDBHelper.TrainingDaysFromDB.dateForChronology = null
        navController!!.navigate(R.id.navigationChronology)
    }

    private fun switchToWeight() = navController!!.navigate(R.id.navigationWeight)

    fun switchToChronologyFromCalendar(date: String?) {
        currentItem = R.id.navigationChronology
        MyHelper.MyDBHelper.TrainingDaysFromDB.dateForChronology = date
        navController!!.navigate(R.id.navigationChronology)
    }
}