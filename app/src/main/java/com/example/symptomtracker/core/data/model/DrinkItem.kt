package com.example.symptomtracker.core.data.model

import com.example.symptomtracker.core.database.model.DrinkItemEntity
import com.example.symptomtracker.core.domain.model.DrinkItem

fun DrinkItem.asEntity(): DrinkItemEntity = DrinkItemEntity(id, name)
