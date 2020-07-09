package com.google.cloud.opentelemetry.example.trace;

import com.google.cloud.opentelemetry.trace.TraceExporter;
import com.google.devtools.cloudtrace.v2.AttributeValue;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.export.SimpleSpansProcessor;
import java.util.HashMap;
import java.util.Map;

public class TraceExample {
  private static final String PROJECT_ID = "project";
  private static final Map<String, AttributeValue> FIXED_ATTRIBUTES = new HashMap<>();

  private static void setupTraceExporter() {
    // Export traces to Google Cloud Trace
    TraceExporter traceExporter = new TraceExporter(PROJECT_ID, null, FIXED_ATTRIBUTES);

    // Set to process the spans by the Jaeger Exporter
    OpenTelemetrySdk.getTracerProvider()
        .addSpanProcessor(SimpleSpansProcessor.newBuilder(traceExporter).build());
  }

  public static void main(String[] args) {
    setupTraceExporter();
    System.out.println("TODO: create some spans");
  }
}
