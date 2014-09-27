#include <pebble.h>

static Window *window;
static TextLayer *text_layer;

enum {
    DEPARTURE_TIME
};

static void window_load(Window *window) {
  Layer *window_layer = window_get_root_layer(window);
  GRect bounds = layer_get_bounds(window_layer);

  text_layer = text_layer_create((GRect) { .origin = { 0, 5 }, .size = { bounds.size.w, 70 } });
  text_layer_set_text_alignment(text_layer, GTextAlignmentCenter);
  text_layer_set_font(text_layer,
      fonts_load_custom_font(resource_get_handle(RESOURCE_ID_FONT_ROBOTO_BOLD_CONDENSED_SUBSET_60)));
  layer_add_child(window_layer, text_layer_get_layer(text_layer));
}

static void window_unload(Window *window) {
  text_layer_destroy(text_layer);
}

void out_sent_handler(DictionaryIterator *sent, void *context) {
   // outgoing message was delivered
 }


 void out_failed_handler(DictionaryIterator *failed, AppMessageResult reason, void *context) {
   // outgoing message failed
 }


 void in_received_handler(DictionaryIterator* received, void* context) {
    static char message[] = "10:23";
   // incoming message received
   Tuple* text_tuple = dict_find(received, DEPARTURE_TIME);

   if (text_tuple) {
       snprintf(message, sizeof(message), "%s", text_tuple->value->cstring);
       text_layer_set_text(text_layer, message);
   }
 }


 void in_dropped_handler(AppMessageResult reason, void *context) {
   // incoming message dropped
 }

 static void init(void) {
  window = window_create();

  window_set_window_handlers(window, (WindowHandlers) {
    .load = window_load,
    .unload = window_unload,
  });
  const bool animated = true;
  window_stack_push(window, animated);
}

static void deinit(void) {
  window_destroy(window);
}

int main(void) {
    const uint32_t inbound_size = 64;
    const uint32_t outbound_size = 64;
    init();
    app_message_register_inbox_received(in_received_handler);
    app_message_register_inbox_dropped(in_dropped_handler);
    app_message_register_outbox_sent(out_sent_handler);
    app_message_register_outbox_failed(out_failed_handler);
    app_message_open(inbound_size, outbound_size);

    app_event_loop();
    deinit();
}
