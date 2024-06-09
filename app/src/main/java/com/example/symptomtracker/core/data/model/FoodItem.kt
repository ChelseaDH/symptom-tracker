package com.example.symptomtracker.core.data.model

import com.example.symptomtracker.core.database.model.FoodItemEntity
import com.example.symptomtracker.core.model.FoodItem

fun FoodItem.asEntity(): FoodItemEntity = FoodItemEntity(id, name)