package com.github.cb372.phonehome.models

case class ErrorPayload(app: String, 
                        url: String, 
                        errorName: String, 
                        errorMessage: String, 
                        errorFile: Option[String], 
                        errorLine: Option[Int], 
                        userAgent: String,
                        customFields: Option[Map[String, String]])

