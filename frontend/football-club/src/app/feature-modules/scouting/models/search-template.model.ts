export interface TemplatePartSave {
  metricId: number;
  weight: number;
}

export interface SearchTemplateSave {
  templateName: string;
  parts: TemplatePartSave[];
}

export interface TemplatePart {
  id: number;
  searchTemplateId: number;
  metricId: number;
  metricName: string;
  weight: number;
}

export interface SearchTemplate {
  id: number;
  templateName: string;
  creatorId: number;
  creatorName: string;
  parts: TemplatePart[];
}