import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CacheService {
  private readonly apiUrl = '/api/cache';

  constructor(private readonly http: HttpClient) {}

  flushAll(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/flushAll`, {});
  }

  flushByName(cacheName: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/flushByName`, { cacheName });
  }
}
