import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { JwtModule } from '@auth0/angular-jwt';

import { AppRoutingModule } from './infrastructure/routing/app-routing.module';
import { AppComponent } from './app.component';
import { JwtInterceptor } from './infrastructure/auth/jwt/jwt.interceptor';

import { MatchModule } from './feature-modules/match/match.module';
import { TicketSalesModule } from './feature-modules/ticket-sales/ticket-sales.module';

import { CommonModule } from '@angular/common';
import { AuthModule } from './infrastructure/auth/components/auth.module';
import { LayoutModule } from './feature-modules/layout/layout.module';
import { ScoutingPlayerListComponent } from './feature-modules/scouting/components/scouting-player-list/scouting-player-list.component';
import { ScoutingModule } from './feature-modules/scouting/scouting.module';
import { NgChartsModule } from 'ng2-charts';

export function tokenGetter() {
  return localStorage.getItem('access-token');
}

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    AppRoutingModule,
    MatchModule,
    TicketSalesModule,
    AuthModule,
    LayoutModule,
    ScoutingModule,
    NgChartsModule,
    JwtModule.forRoot({
      config: {
        tokenGetter,
        allowedDomains: ['localhost:8080']
      }
    })
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
