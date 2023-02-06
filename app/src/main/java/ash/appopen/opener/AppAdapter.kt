package ash.appopen.opener

import android.content.Context
import android.content.pm.ResolveInfo
import androidx.recyclerview.widget.RecyclerView
import ash.appopen.opener.AppAdapter.AppViewHolder
import android.widget.Filter.FilterResults
import android.view.ViewGroup
import android.view.LayoutInflater
import ash.appopen.opener.R
import android.graphics.drawable.Drawable
import android.content.pm.PackageManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import android.view.View.OnLongClickListener
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class AppAdapter(var mAppList: ArrayList<InstalledApp>) : RecyclerView.Adapter<AppViewHolder>()
     {

    var context: Context? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val context2 = parent.context
        context = context2
        return AppViewHolder(
            LayoutInflater.from(context2).inflate(R.layout.approwcontainer, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val pos = holder.adapterPosition
//        val name = mAppList[pos].activityInfo.loadLabel(context!!.packageManager).toString()
//        val packname = mAppList[pos].activityInfo.packageName
//        if (packname != context!!.packageName) {
//            var icon: Drawable? = null
//            try {
//                icon =
//                    context!!.packageManager.getApplicationIcon(mAppList[pos].activityInfo.packageName)
//            } catch (e: PackageManager.NameNotFoundException) {
//                e.printStackTrace()
//            }
//            Glide.with(context!!).load(icon)
//                .into(holder.icon_view)
//            holder.appname.text = name
//            holder.packagename.text = packname
//        }
        holder.appname.text = mAppList[pos].appName
        holder.packagename.text = mAppList[pos].pkgName
        Glide.with(context!!).load(mAppList[pos].appIcon)
                .into(holder.icon_view)
        holder.myView.setOnLongClickListener { v ->
            showOptionMenu(v, pos)
            true
        }
        holder.myView.setOnClickListener { openApp(pos) }
    }

    fun openApp(position: Int) {
        try {
            context!!.startActivity(
                context!!.packageManager.getLaunchIntentForPackage(
                    mAppList[position].pkgName
                )
            )
        } catch (e: Exception) {
            Toast.makeText(context, "Could not find application!", Toast.LENGTH_SHORT).show()
        }
    }
         fun filterList(list: ArrayList<InstalledApp>){
             mAppList = list
             notifyDataSetChanged()

         }

    fun showOptionMenu(view: View, pos: Int) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.popupmenu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            val itemId = item.itemId
            if (itemId == R.id.open) {
                try {
                    context!!.startActivity(
                        context!!.packageManager.getLaunchIntentForPackage(
                            mAppList[pos]!!.pkgName
                        )
                    )
                    return@OnMenuItemClickListener true
                } catch (e: Exception) {
                    Toast.makeText(context, "Could not find application!", Toast.LENGTH_SHORT)
                        .show()
                    return@OnMenuItemClickListener true
                }
            } else if (itemId != R.id.uninstall) {
                true
            } else {
                try {
                    val intent = Intent("android.intent.action.DELETE")
                    val PACKAPP = mAppList[pos]!!.pkgName
                    intent.data = Uri.parse("package:$PACKAPP")
                    context!!.startActivity(intent)
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (!appInstalledOrNot(PACKAPP) && pos != -1) {
                            mAppList.removeAt(pos)
                            notifyDataSetChanged()
                        }
                    }, 8000)
                    true
                } catch (e2: Exception) {
                    Toast.makeText(context, "Could not uninstall app", Toast.LENGTH_SHORT).show()
                    true
                }
            }
        })
        popupMenu.show()
    }

    /* access modifiers changed from: private */
    fun appInstalledOrNot(uri: String?): Boolean {
        return try {
            context!!.packageManager.getPackageInfo(uri!!, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    override fun getItemCount(): Int {
        return mAppList.size
    }


    class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var appname: TextView
        var icon_view: ImageView
        var myView: View
        var packagename: TextView

        init {
            icon_view = itemView.findViewById<View>(R.id.app_icon) as ImageView
            appname = itemView.findViewById<View>(R.id.App_Name) as TextView
            packagename = itemView.findViewById<View>(R.id.App_package_name) as TextView
            myView = itemView
        }
    }
}