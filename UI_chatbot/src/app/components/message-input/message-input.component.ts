import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-message-input',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './message-input.component.html',
  styleUrls: ['./message-input.component.css']
})
export class MessageInputComponent {
  text = '';
  @Output() sendMessage = new EventEmitter<string>();

  onEnter(event: any) {
  if (!event.shiftKey) {
    event.preventDefault();
    this.send();
  }
}

  send() {
    if (!this.text.trim()) return;
    this.sendMessage.emit(this.text.trim());
    this.text = '';
  }
}
