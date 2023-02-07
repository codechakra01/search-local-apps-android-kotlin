package ash.appopen.opener

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import ash.appopen.opener.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    var mAdapter: AppAdapter? = null
    var pkgAppsList: ArrayList<ResolveInfo>? = null
    var appList : ArrayList<InstalledApp>? = null
    private lateinit var mainBinding: ActivityMainBinding
    var firstTimeRan =  false
    val executor = Executors.newSingleThreadExecutor()
    val handler = Handler(Looper.getMainLooper())
    /* access modifiers changed from: protected */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        appList = ArrayList()

        mainBinding.progressCard.visibility = View.VISIBLE
        executor.execute {
            firstTimeRan = true

            val intent = Intent("android.intent.action.MAIN", null as Uri?)
            intent.addCategory("android.intent.category.LAUNCHER")
            pkgAppsList = packageManager.queryIntentActivities(intent, 0) as ArrayList<ResolveInfo>
            pkgAppsList!!.forEach()
            {
                if (it.activityInfo.loadLabel(packageManager).toString().lowercase() != "search local apps"){
                    appList?.add(InstalledApp(it.activityInfo.loadLabel(packageManager).toString(),
                        it.activityInfo.packageName, packageManager.getApplicationIcon(it.activityInfo.packageName)))
                }

            }
            handler.post {
                mainBinding.progressCard.visibility = View.GONE
                mainBinding.AppRecyclerContainer.layoutManager = LinearLayoutManager(this)
                appList?.let {
                    val appAdapter = AppAdapter(it)
                    mAdapter = appAdapter
                    mainBinding.AppRecyclerContainer.adapter = appAdapter
                }
            }


        }


        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().subscribeToTopic("search_app_notification")
            .addOnCompleteListener { task ->

            }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val item = menu.findItem(R.id.search_button)
        val about = menu.findItem(R.id.about)
        val support = menu.findItem(R.id.support_me)
        val refresh = menu.findItem(R.id.refresh_lay)
        about.setOnMenuItemClickListener {
            aboutDialog
            true
        }
        support.setOnMenuItemClickListener {
            supportMe()
            true
        }
        refresh.setOnMenuItemClickListener {
            refreshRecyclerView()
            Toast.makeText(this@MainActivity, "Apps refreshed", Toast.LENGTH_SHORT).show()
            true
        }
        val searchView: SearchView = item.actionView as SearchView

        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                    filter(newText)
                return false
            }
        })

        return true//super.onCreateOptionsMenu(menu)
    }
    private fun filter(text: String){
        val filteredlist: ArrayList<InstalledApp> = ArrayList()

        // running a for loop to compare elements.
        for (item in appList!!) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.appName.lowercase().contains(text.lowercase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }
        if (filteredlist.isNotEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            mAdapter?.filterList(filteredlist)
        }
    }

    /* access modifiers changed from: private */
    fun refreshRecyclerView() {
        executor.execute {
            if (pkgAppsList != null) {
                val intent = Intent("android.intent.action.MAIN", null as Uri?)
                intent.addCategory("android.intent.category.LAUNCHER")
                pkgAppsList!!.clear()
                appList?.clear()
                pkgAppsList = packageManager.queryIntentActivities(intent, 0) as ArrayList<ResolveInfo>
                pkgAppsList!!.forEach()
                {
                    if (it.activityInfo.loadLabel(packageManager).toString().lowercase() != "search local apps"){
                        appList?.add(InstalledApp(it.activityInfo.loadLabel(packageManager).toString(),
                            it.activityInfo.packageName, packageManager.getApplicationIcon(it.activityInfo.packageName)))
                    }
                }
                handler.post {
                    mAdapter = null
                    appList?.let {
                        val appAdapter = AppAdapter(it)
                        mAdapter = appAdapter
                        mainBinding.AppRecyclerContainer.adapter = appAdapter
                    }
                }

            }
        }



    }


    override fun onResume() {
        if (firstTimeRan){
            firstTimeRan = false
        }else{
            refreshRecyclerView()

        }
        super.onResume()
    }

    /* access modifiers changed from: private */
    fun supportMe() {
        val builderc = AlertDialog.Builder(this)
        val viewc = LayoutInflater.from(this).inflate(R.layout.contacts, null as ViewGroup?)
        builderc.setView(viewc)
        (viewc.findViewById<View>(R.id.Insta_button) as ImageButton).setOnClickListener {
            try {
                this@MainActivity.startActivity(
                    Intent(
                        "android.intent.action.VIEW",
                        Uri.parse("https://www.instagram.com/code_chakra/")
                    )
                )
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Couldn't find an app to open!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        builderc.setNegativeButton(
            "close" as CharSequence
        ) { dialog, _ -> dialog.dismiss() }
        builderc.create().show()
    }

    /* access modifiers changed from: private */
    val aboutDialog: Unit
        get() {
            val builder = AlertDialog.Builder(this)
            builder.setView(LayoutInflater.from(this).inflate(R.layout.about, null as ViewGroup?))
            builder.setNegativeButton(
                "close" as CharSequence,
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        dialog.dismiss()
                    }
                } as DialogInterface.OnClickListener)
            builder.create().show()
        }
}