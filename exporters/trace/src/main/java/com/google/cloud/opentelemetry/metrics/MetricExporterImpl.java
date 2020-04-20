package com.google.cloud.opentelemetry.metrics;

import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import java.util.Collection;

public class MetricExporterImpl implements MetricExporter {

  @Override
  public ResultCode export(Collection<MetricData> metrics) {
    return null;
  }

  @Override
  public void shutdown() {

  }
}
