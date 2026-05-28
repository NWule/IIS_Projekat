import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

import { MatchSearchComponent } from './components/match-search/match-search.component';
import { StadiumMapComponent } from './components/stadium-map/stadium-map.component';
import { PurchaseFormComponent } from './components/purchase-form/purchase-form.component';
import { MyTicketsComponent } from './components/my-tickets/my-tickets.component';

import { ZoneListComponent } from './components/zone-list/zone-list.component';
import { ZoneFormComponent } from './components/zone-form/zone-form.component';
import { SeatListComponent } from './components/seat-list/seat-list.component';
import { TicketTypeListComponent } from './components/ticket-type-list/ticket-type-list.component';
import { TicketTypeFormComponent } from './components/ticket-type-form/ticket-type-form.component';

@NgModule({
  declarations: [
    MatchSearchComponent,
    StadiumMapComponent,
    PurchaseFormComponent,
    MyTicketsComponent,
    ZoneListComponent,
    ZoneFormComponent,
    SeatListComponent,
    TicketTypeListComponent,
    TicketTypeFormComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule
  ]
})
export class TicketSalesModule {}
