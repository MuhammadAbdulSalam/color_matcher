package com.salam.color_matcher

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.salam.color_matcher.camera.CameraActivity
import com.salam.color_matcher.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnCamera.setOnClickListener{
            val intent = Intent(this, CameraActivity::class.java)
            startActivityForResult(intent, 1)
        }
        binding.btnMatch.setOnClickListener{

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if(requestCode == 1){
            data?.let {
                val color = it.getIntExtra("result", 0)
                binding.ivResults.setBackgroundColor(color)

                val blueTowelbBitmap =  binding.ivBlueColor.drawable.toBitmap()
                val blueTowelPalette = Palette.from(blueTowelbBitmap).generate()
                val blueTowelColor = blueTowelPalette.getVibrantColor(ContextCompat.getColor(this, R.color.purple_200))
                val blueTowelDistance = getDistance(blueTowelColor, color)

                val redTowel =  binding.ivMultiColor.drawable.toBitmap()
                val redTowelPallet = Palette.from(redTowel).generate()
                val redTowelColor = redTowelPallet.getVibrantColor(ContextCompat.getColor(this, R.color.purple_200))
                val redTowelDistance  = getDistance(redTowelColor, color)

                Log.d("-------", "----------------------------------------$blueTowelDistance")
                Log.d("-------", "----------------------------------------$redTowelDistance")

                if(blueTowelDistance < 10000){
                    binding.tvResults.text = "Blue towel is a good match bitch!!!  and matches ${getMatchPercent(blueTowelDistance)}%"
                    binding.ivImageColor.setBackgroundColor(blueTowelColor)
                }
                else if(redTowelDistance < 10000){
                    binding.tvResults.text = "Red is a good match and matches ${getMatchPercent(redTowelDistance)}%"
                    binding.ivImageColor.setBackgroundColor(redTowelColor)
                }
                else{
                    binding.tvResults.text = "no match found. try again"
                }

            }
        }


    }

    fun getMatchPercent(value: Int): Int{
       if(value >= 9000){
           return 20
       }
        else if(value >= 8000){
            return 30
        }
       else if(value >= 7000){
           return 40
       }
       else if(value >= 6000){
           return 50
       }
       else if(value >= 5000){
           return 60
       }
       else if(value >= 4000){
           return 70
       }
       else if(value >= 3000){
           return 80
       }
       else if(value >= 2000){
           return 90
       }
       else if(value >= 1000){
           return 95
       }
        else {
            return 98
        }


    }

    //((r2 - r1)2 + (g2 - g1)2 + (b2 - b1)2)1/2
    fun getDistance(hexColor: Int, matchToColor: Int): Int{
        val initColor: Int = hexColor
        val initColor_R = initColor shr 16 and 0xFF
        val initColor_G = initColor shr 8 and 0xFF
        val initColor_B = initColor shr 0 and 0xFF

        val myColor: Int = matchToColor
        val myColor_R = myColor shr 16 and 0xFF
        val myColor_G = myColor shr 8 and 0xFF
        val myColor_B = myColor shr 0 and 0xFF

        val differenceR = myColor_R - initColor_R
        val differenceG = myColor_G - initColor_G
        val differenceB = myColor_B - initColor_B

        val differenceSquare_R = differenceR * differenceR
        val differenceSquare_G = differenceG * differenceG
        val differenceSquare_B = differenceB * differenceB

        val additionColors = differenceSquare_R + differenceSquare_G + differenceSquare_B

        val distance = additionColors / 2


        return distance
    }
}