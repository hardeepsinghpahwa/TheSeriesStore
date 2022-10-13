package com.example.login

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.baoyachi.stepview.HorizontalStepView
import com.baoyachi.stepview.bean.StepBean
import android.content.Intent
import maes.tech.intentanim.CustomIntent
import android.view.*
import androidx.databinding.DataBindingUtil
import com.example.login.databinding.ActivityOrderPaymentDetailsBinding
import com.example.login.databinding.ActivityOrderPaymentStatusBinding
import java.util.ArrayList

class OrderPaymentStatus : AppCompatActivity() {

    lateinit var binding:ActivityOrderPaymentStatusBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_payment_status)
        binding.executePendingBindings()

        val stepsBeanList: MutableList<StepBean> = ArrayList()
        val stepBean0: StepBean = StepBean("Cart", 1)
        val stepBean1: StepBean = StepBean("Address", 1)
        val stepBean2: StepBean = StepBean("Payment", 1)
        val stepBean3: StepBean = StepBean("Placed", 1)
        stepsBeanList.add(stepBean0)
        stepsBeanList.add(stepBean1)
        stepsBeanList.add(stepBean2)
        stepsBeanList.add(stepBean3)
        binding.stepsView.setStepViewTexts(stepsBeanList) //???
                .setTextSize(12) //set textSize
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey)) //??StepsViewIndicator??????
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray)) //??StepsViewIndicator???????
                .setStepViewComplectedTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey)) //??StepsView text??????
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey)) //??StepsView text???????
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.checkedd)) //??StepsViewIndicator CompleteIcon
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.outline_radio_button_checked_24)) //??StepsViewIndicator DefaultIcon
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.uncheckedd))
        binding.continueshopping.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                startActivity(Intent(this@OrderPaymentStatus, Home::class.java))
                CustomIntent.customType(this@OrderPaymentStatus, "bottom-to-up")
            }
        })
        binding.vieworder.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {}
        })
    }

    public override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@OrderPaymentStatus, Home::class.java))
        CustomIntent.customType(this@OrderPaymentStatus, "bottom-to-up")
    }
}