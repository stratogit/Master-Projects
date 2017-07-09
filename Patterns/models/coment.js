var mongoose = require('mongoose'),

    Schema = mongoose.Schema;

var CommentSchema = new Schema({

    lastrating	: { type: Number },
    patternid	: { type: String, required: true },    
    namecoment	: { type: String},
    coment : 	{ type: String },
    email	: 	{ type: String }
});

module.exports = mongoose.model('Coment', CommentSchema);
