import { Component } from '@angular/core';
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
  imports: [MatToolbarModule, MatButtonModule, MatIconModule],
  standalone: true
})
export class NavbarComponent {

}
