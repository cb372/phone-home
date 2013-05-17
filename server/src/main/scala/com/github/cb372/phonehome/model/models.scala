package com.github.cb372.phonehome.models

case class ErrorPayload(app: String, 
                        url: String, 
                        error: String, 
                        userAgent: String,
                        customFields: Option[Map[String, String]])

