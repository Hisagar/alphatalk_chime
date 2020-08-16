/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.example.alphatalkchime.data

import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.VideoTileState

data class VideoCollectionTile(
    val attendeeName: String,
    val videoTileState: VideoTileState
)
