export interface Metric {
  id: number;
  name: string;
  category: string;
}

export interface ValuedMetricSave {
  reportId: number;
  metricId: number;
  value: number;
}