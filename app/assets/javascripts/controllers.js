'use strict';

/** Controllers */
angular.module('sseChat.controllers', ['sseChat.services']).
    controller('ChatCtrl', function ($scope, $http, chatModel) {
        $scope.rooms = chatModel.getRooms();

        var msgs = [];
        $scope.msgs = function () {
            return _.last(JSON.parse(JSON.stringify(msgs)), 3);
        };

        $scope.inputText = "";
        $scope.user = "Jane Doe #" + Math.floor((Math.random() * 100) + 1);
        $scope.currentRoom = $scope.rooms[0];

        /** change current room, restart EventSource connection */
        $scope.setCurrentRoom = function (room) {
            $scope.currentRoom = room;
            $scope.chatFeed.close();
            msgs = [];
            $scope.listen();
        };

        /** posting chat text to server */
        $scope.submitMsg = function () {
            $http.post("/chat", { text: $scope.inputText, user: $scope.user,
                time: (new Date()).toUTCString(), room: $scope.currentRoom.value });
            $scope.inputText = "";
        };

        /** handle incoming messages: add to messages array */
        $scope.addMsg = function (msg) {
            $scope.$apply(function () { msgs.push(JSON.parse(msg.data)); });
        };

        /** start listening on messages from selected room */
        $scope.listen = function () {
            $scope.chatFeed = new EventSource("/chatFeed/" + $scope.currentRoom.value);
            $scope.chatFeed.addEventListener("message", $scope.addMsg, false);
        };

        $scope.listen();
    });
