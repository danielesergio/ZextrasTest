package com.danielesergio.zextrastest.model.datasource

class DelegateDataSource (private val delegate: DataSource): DataSource by delegate