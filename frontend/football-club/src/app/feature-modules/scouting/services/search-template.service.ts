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

  getMyTemplates(): Observable<SearchTemplate[]> {
    return this.http.get<SearchTemplate[]>(`${this.baseUrl}/my`);
  }
}