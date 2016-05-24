ws = new WebSocket("ws://localhost:8080/api/gameplay");
ws.onopen = function() {
  alert("Соединение установлено.");
};
ws.onmessage = function(event) {
  alert("Получены данные " + event.data);
};
ws.onerror = function(error) {
  alert("Ошибка " + error.message);
};

ws.send("{\"action\":\"getWord\"}")

ws.send("{\"action\":\"checkWord\",\"id\":1,\"word\":\"cat\"}")