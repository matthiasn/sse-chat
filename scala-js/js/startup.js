ScalaJS.modules.com_matthiasnehlsen_sseChat_App().main();

var ScalaApp = {};
ScalaApp.InterOp = ScalaJS.modules.com_matthiasnehlsen_sseChat_InterOp();

ScalaApp["setUser"] = ScalaApp.InterOp.setUser__T__V;
ScalaApp["setRoom"] = ScalaApp.InterOp.setRoom__T__V;
ScalaApp["undo"] = ScalaApp.InterOp.undo__V;
ScalaApp["undoAll"] = ScalaApp.InterOp.undoAll__T__V;
ScalaApp["submitMsg"] = ScalaApp.InterOp.addMsg__Lcom_matthiasnehlsen_sseChat_ChatMsgTrait__V;
ScalaApp["triggerReact"] = ScalaApp.InterOp.triggerReact__V;

window['ScalaApp'] = ScalaApp;
