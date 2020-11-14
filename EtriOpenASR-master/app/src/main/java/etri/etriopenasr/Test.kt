package etri.etriopenasr

class Test {
    let url = await firebase.storage().ref('Audio/English/United_States-OED-' + i +'/' + $scope.word.word + ".mp3").getDownloadURL();
}