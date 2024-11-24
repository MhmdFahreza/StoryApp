package com.muhammadfahreza.storyapp.data.response

import com.google.gson.annotations.SerializedName

data class StoryResponse(

	@field:SerializedName("listStory")
	val listStory: List<ListStoryItem?>? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ListStoryItem(
	@SerializedName("photoUrl")
	val photoUrl: String? = null,

	@SerializedName("createdAt")
	val createdAt: String? = null,

	@SerializedName("name")
	val name: String? = null,

	@SerializedName("description")
	val description: String? = null,

	@SerializedName("lon")
	val lon: Double? = null,

	@SerializedName("id")
	val id: String? = null,

	@SerializedName("lat")
	val lat: Double? = null
)
