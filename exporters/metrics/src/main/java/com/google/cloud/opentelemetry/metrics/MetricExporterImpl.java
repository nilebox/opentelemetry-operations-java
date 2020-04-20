package com.google.cloud.opentelemetry.metrics;

import com.google.monitoring.v3.CreateTimeSeriesRequest;
import com.google.monitoring.v3.TimeSeries;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import io.opentelemetry.trace.Span;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MetricExporterImpl implements MetricExporter {

  @Override
  public ResultCode export(Collection<MetricData> metrics) {
    List<TimeSeries> timeSeriesList = new ArrayList<>(metrics.size());
    for (MetricData metric : metrics) {
      timeSeriesList.addAll(
          StackdriverExportUtils.createTimeSeriesList(
              metric, monitoredResource, domain, projectName.getProject(), constantLabels));
    }

    Span span = tracer.getCurrentSpan();
    for (List<TimeSeries> batchedTimeSeries :
        Lists.partition(timeSeriesList, MAX_BATCH_EXPORT_SIZE)) {
      span.addAnnotation("Export Stackdriver TimeSeries.");
      try {
        CreateTimeSeriesRequest request =
            CreateTimeSeriesRequest.newBuilder()
                .setName(projectName.toString())
                .addAllTimeSeries(batchedTimeSeries)
                .build();
        metricServiceClient.createTimeSeries(request);
        span.addAnnotation("Finish exporting TimeSeries.");
      } catch (ApiException e) {
        logger.log(Level.WARNING, "ApiException thrown when exporting TimeSeries.", e);
        span.setStatus(
            Status.CanonicalCode.valueOf(e.getStatusCode().getCode().name())
                .toStatus()
                .withDescription(
                    "ApiException thrown when exporting TimeSeries: "
                        + StackdriverExportUtils.exceptionMessage(e)));
      } catch (Throwable e) {
        logger.log(Level.WARNING, "Exception thrown when exporting TimeSeries.", e);
        span.setStatus(
            Status.UNKNOWN.withDescription(
                "Exception thrown when exporting TimeSeries: "
                    + StackdriverExportUtils.exceptionMessage(e)));
      }
    }
  }
  }

  @Override
  public void shutdown() {

  }
}
