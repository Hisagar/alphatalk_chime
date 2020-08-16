/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.alphatalkchime.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.amazonaws.services.chime.sdk.meetings.audiovideo.AudioVideoFacade
import com.amazonaws.services.chime.sdk.meetings.session.CreateAttendeeResponse
import com.amazonaws.services.chime.sdk.meetings.session.CreateMeetingResponse
import com.amazonaws.services.chime.sdk.meetings.session.DefaultMeetingSession
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionConfiguration
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionCredentials
import com.amazonaws.services.chime.sdk.meetings.utils.logger.ConsoleLogger
import com.amazonaws.services.chime.sdk.meetings.utils.logger.LogLevel
import com.example.alphatalkchime.R
import com.example.alphatalkchime.data.JoinMeetingResponse
import com.example.alphatalkchime.fragment.DeviceManagementFragment
import com.example.alphatalkchime.fragment.MeetingFragment
import com.example.alphatalkchime.model.MeetingSessionModel
import com.google.gson.Gson
import kotlin.properties.Delegates

class MeetingActivity : AppCompatActivity(),
    DeviceManagementFragment.DeviceManagementEventListener,
    MeetingFragment.RosterViewEventListener {

    private val logger = ConsoleLogger(LogLevel.DEBUG)
    private val gson = Gson()
    private val meetingSessionModel: MeetingSessionModel by lazy { ViewModelProvider(this)[MeetingSessionModel::class.java] }
    private lateinit var meetingId: String
    private lateinit var name: String
    private var isVideoCall by Delegates.notNull<Boolean>()

    private val TAG = "InMeetingActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting)
        meetingId = intent.getStringExtra(HomeActivity.MEETING_ID_KEY) as String
        isVideoCall = intent.getBooleanExtra(HomeActivity.ISVIDEON_KEY,false)
        name = intent.getStringExtra("userName") as String
        if (savedInstanceState == null) {
            val meetingResponseJson =
                intent.getStringExtra(HomeActivity.MEETING_RESPONSE_KEY) as String
            val sessionConfig = createSessionConfiguration(meetingResponseJson)
            val meetingSession = sessionConfig?.let {
                logger.info(TAG, "Creating meeting session for meeting Id: $meetingId")
                DefaultMeetingSession(
                    it,
                    logger,
                    applicationContext
                )
            }

            if (meetingSession == null) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.user_notification_meeting_start_error),
                    Toast.LENGTH_LONG
                ).show()
                finish()
            } else {
                meetingSessionModel.setMeetingSession(meetingSession)
            }

            val deviceManagementFragment = DeviceManagementFragment.newInstance(meetingId, name)
            supportFragmentManager
                .beginTransaction()
                .add(R.id.root_layout, deviceManagementFragment, "deviceManagement")
                .commit()
        }
    }

    override fun onJoinMeetingClicked() {
        val rosterViewFragment = MeetingFragment.newInstance(meetingId,isVideoCall)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.root_layout, rosterViewFragment, "rosterViewFragment")
            .commit()
    }

    override fun onLeaveMeeting() {
        onBackPressed()
    }

    override fun onBackPressed() {
        meetingSessionModel.audioVideo.stop()
        super.onBackPressed()
    }

    fun getAudioVideo(): AudioVideoFacade = meetingSessionModel.audioVideo

    fun getMeetingSessionCredentials(): MeetingSessionCredentials = meetingSessionModel.credentials

    private fun urlRewriter(url: String): String {
        // You can change urls by url.replace("example.com", "my.example.com")
        return url
    }

    private fun createSessionConfiguration(response: String?): MeetingSessionConfiguration? {
        if (response.isNullOrBlank()) return null

        return try {
            val joinMeetingResponse = gson.fromJson(response, JoinMeetingResponse::class.java)
            MeetingSessionConfiguration(
                CreateMeetingResponse(joinMeetingResponse.joinInfo.meetingResponse.meeting),
                CreateAttendeeResponse(joinMeetingResponse.joinInfo.attendeeResponse.attendee),
                ::urlRewriter
            )
        } catch (exception: Exception) {
            logger.error(
                TAG,
                "Error creating session configuration: ${exception.localizedMessage}"
            )
            null
        }
    }
}
