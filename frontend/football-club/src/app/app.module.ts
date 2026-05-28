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

import { LoginComponent } from './infrastructure/auth/components/login/login.component';
import { RegisterComponent } from './infrastructure/auth/components/register/register.component';

export function tokenGetter() {
  return localStorage.getItem('access-token');
}

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    AppRoutingModule,
    MatchModule,
    TicketSalesModule,
    JwtModule.forRoot({
      config: {
        tokenGetter,
        allowedDomains: ['localhost:8080']
      }
    })
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
