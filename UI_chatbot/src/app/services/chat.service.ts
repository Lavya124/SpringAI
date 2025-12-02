import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  private baseUrl = "http://localhost:8080/chat/getChat";

  constructor(private http: HttpClient) {}

  sendMessage(userId: string, chatId: string, message: string): Observable<string> {
    const url = `${this.baseUrl}?userId=${encodeURIComponent(userId)}&chatId=${encodeURIComponent(chatId)}&message=${encodeURIComponent(message)}`;
    return this.http.get(url, { responseType: 'text' });
  }
}
