import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-profile-panel',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile-panel.component.html',
  styleUrls: ['./profile-panel.component.css']
})
export class ProfilePanelComponent {
  @Input() profile: { [key:string]: string } = {};
  get profileKeys() { return Object.keys(this.profile); }
}
