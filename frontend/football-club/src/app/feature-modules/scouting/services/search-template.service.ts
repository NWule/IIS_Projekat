import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SearchTemplate, SearchTemplateSave } from '../models/search-template.model';
import { environment } from 'src/env/environment';

@Injectable({
  providedIn: 'root'
})
export class SearchTemplateService {
  private baseUrl = environment.apiHost + 'search-templates'

  constructor(private http: HttpClient) {}

  createTemplate(template: SearchTemplateSave): Observable<SearchTemplate> {
    return this.http.post<SearchTemplate>(this.baseUrl, template);
  }

  getTemplateById(id: number): Observable<SearchTemplate> {
    return this.http.get<SearchTemplate>(`${this.baseUrl}/${id}`);
  }

  deleteTemplate(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getMyTemplates(): Observable<SearchTemplate[]> {
    return this.http.get<SearchTemplate[]>(`${this.baseUrl}/my`);
  }

  updateTemplate(id: number, dto: SearchTemplateSave): Observable<SearchTemplate> {
    return this.http.put<SearchTemplate>(`${this.baseUrl}/${id}`, dto);
  }
}