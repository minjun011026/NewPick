package com.unit_3.sogong_test

import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.unit_3.sogong_test.databinding.ActivityMapViewBinding
import jxl.Workbook
import jxl.read.biff.BiffException
import java.io.IOException
import java.util.Locale


class MapViewActivity : AppCompatActivity() , OnMapReadyCallback, OnItemClickListener {
    private val LOCATION_PERMISSION_REQUEST_CODE = 5000

    private val PERMISSIONS = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val nearCity = ArrayList<String>()
    private lateinit var binding: ActivityMapViewBinding
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var listViewAdapter : ListViewAdapter
    private lateinit var addressBtn1 : Button
    private lateinit var addressBtn2 : Button
    private lateinit var bottomSheetDialog: BottomSheetDialog
    var addressName : String = ""
    private val marker = Marker()
    private var curlatitude = 0.0
    private var curlongitude = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map_view)
        if (!hasPermission()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            initMapView()
        }
        addressBtn1 = findViewById<Button>(R.id.addressBtn1)
        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(R.layout.layout_bottom_sheet, null)
        listViewAdapter = ListViewAdapter(this, nearCity, bottomSheetDialog, this)

        addressBtn1.setOnClickListener{
            if(addressName != ""){
                readExcel(addressName)
                bottomSheetView.findViewById<ListView>(R.id.listView).adapter = listViewAdapter
                bottomSheetDialog.setContentView(bottomSheetView)
                addressBtn1.text="~"
                bottomSheetDialog.show()
                addressName = ""
            }
        }

        addressBtn2 = findViewById<Button>(R.id.addressBtn2)

        addressBtn2.setOnClickListener{
            if(addressName != ""){
                readExcel(addressName)
                bottomSheetView.findViewById<ListView>(R.id.listView).adapter = listViewAdapter
                bottomSheetDialog.setContentView(bottomSheetView)
                addressBtn2.text="~"
                bottomSheetDialog.show()
                addressName = ""
            }
        }

        val saveBtn = findViewById<Button>(R.id.locationSaveBtn)

        saveBtn.setOnClickListener{
            if(addressBtn1.text.toString()!="+") {
                val database = Firebase.database
                val myRef = database.getReference("location").child(Firebase.auth.currentUser!!.uid)
                myRef.push().setValue(addressBtn1.text.toString())
            }
            if(addressBtn2.text.toString()!="+"){
                val database = Firebase.database
                val myRef = database.getReference("location").child(Firebase.auth.currentUser!!.uid)
                myRef.push().setValue(addressBtn2.text.toString())
            }
        }

    }
    override fun onItemClick(item: String){
        if(addressBtn1.text.toString() == "~")
            addressBtn1.text = item
        else if(addressBtn2.text.toString() == "~")
            addressBtn2.text = item
        bottomSheetDialog.dismiss()
    }

    private fun initMapView() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        // fragment의 getMapAsync() 메서드로 OnMapReadyCallback 콜백을 등록하면 비동기로 NaverMap 객체를 얻을 수 있다.
        mapFragment.getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    // hasPermission()에서는 위치 권한이 있을 경우 true를, 없을 경우 false를 반환한다.
    private fun hasPermission(): Boolean {
        for (permission in PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        // 현재 위치
        naverMap.locationSource = locationSource
        // 현재 위치 버튼 기능
        naverMap.uiSettings.isLocationButtonEnabled = true
        // 위치를 추적하면서 카메라도 따라 움직인다.
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        naverMap.setOnMapClickListener { point, coord ->
            marker(coord.latitude, coord.longitude)
        }
    }

    private fun marker(latitude: Double, longitude: Double) {
        marker.position = LatLng(latitude, longitude)
        marker.map = naverMap

        getAddress(latitude, longitude)
    }

    private fun getAddress(latitude: Double, longitude: Double) {
        // Geocoder 선언
        val geocoder = Geocoder(applicationContext, Locale.KOREAN)
        Log.d("checheckchcheck", "getAddress occur")
        // 안드로이드 API 레벨이 33 이상인 경우
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(
                latitude, longitude, 1
            ) { address ->
                if (address.size != 0) {
                    // 반환 값에서 전체 주소만 사용한다.
                    // getAddressLine(0)
                    toast(address[0].locality)
                    addressName=address[0].locality
                    curlatitude = latitude
                    curlongitude = longitude
                }
            }
        } else { // API 레벨이 33 미만인 경우
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses != null) {
                toast(addresses[0].locality)
                addressName=addresses[0].locality
                curlatitude = latitude
                curlongitude = longitude
            }
        }
    }

    private fun toast(text: String) {
        runOnUiThread {
            Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
        }
    }

    fun readExcel(localName: String?) {
        try {
            val `is` = baseContext.resources.assets.open("coordinate.xls")
            val wb = Workbook.getWorkbook(`is`)

            if (wb != null) {
                val sheet = wb.getSheet(2)
                if (sheet != null) {
                    val colTotal = sheet.columns
                    val rowIndexStart = 1
                    val rowTotal = sheet.rows-1
                    Log.d("rowTotal", "$rowTotal")

                    var row = rowIndexStart
                    while (row < rowTotal) {
                        val contents = sheet.getCell(1, row).contents
                        if (contents.contains(localName!!)) {
                            val x = sheet.getCell(5, row).contents
                            val y = sheet.getCell(6, row).contents
                            if(sheet.getCell(2, row).contents.isNotEmpty() && sheet.getCell(3, row).contents.isEmpty()) {
                                if(calculateDistance(curlatitude,curlongitude,x.toDouble(),y.toDouble()) < 20000.0)
                                   nearCity.add(localName+ " " + sheet.getCell(2, row).contents)
                            }
                        }
                        row++
                    }
                    listViewAdapter.notifyDataSetChanged()
                }
            }
        } catch (e: IOException) {
            Log.i("READ_EXCEL1", e.message!!)
            e.printStackTrace()
        } catch (e: BiffException) {
            Log.i("READ_EXCEL1", e.message!!)
            e.printStackTrace()
        }
    }

    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Float {
        val locationA = Location("point A")
        locationA.latitude = lat1
        locationA.longitude = lon1

        val locationB = Location("point B")
        locationB.latitude = lat2
        locationB.longitude = lon2

        return locationA.distanceTo(locationB) //거리값 미터 단위로 반환
    }
}