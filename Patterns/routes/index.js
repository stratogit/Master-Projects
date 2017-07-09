var express = require('express');
var router = express.Router();
var Comment = require('../models/coment');


router.get('/', function (req, res) {
  res.render('index', { title: 'Express' });
});

router.get('/error', function (req, res) {
  res.render('error', { title: 'Express' });
});


//   **************PATERN 1*********
router.get('/pattern1', function (req, res) {
  var query = {"patternid": 1};
 Comment.where(query).then(function(comments){
    console.log(comments);
    res.render('pattern1', { title: 'Express', all_comments: comments });
  });
});


router.post('/pattern1', function (req, res) {
  var comment_data = {
                        "lastrating"  : req.body.rating,
                        "patternid"   : req.body.patternid,
                        "namecoment"  : req.body.commenter,
                        "coment"  : req.body.commentBody,
                        "email"   : req.body.email 
                     };
  console.log(comment_data);
  var comment_object = new Comment(comment_data);
  comment_object.save(
          function(err){
            if(err)
              console.log(err);
            else
              console.log('Comment for pattern1 added to database!');
          });
  res.redirect('/pattern1');
});


//   **************PATERN 2*********
router.get('/pattern2', function (req, res) {
  var query = {"patternid": 2};
 Comment.where(query).then(function(comments){
    console.log(comments);
    res.render('pattern2', { title: 'Express', all_comments: comments });
  });
});

router.post('/pattern2', function (req, res) {
  var comment_data = {
                        "lastrating"  : req.body.rating,
                        "patternid"   : req.body.patternid,
                        "namecoment"  : req.body.commenter,
                        "coment"  : req.body.commentBody,
                        "email"   : req.body.email 
                     };
  console.log(comment_data);
  var comment_object = new Comment(comment_data);
  comment_object.save(
          function(err){
            if(err)
              console.log(err);
            else
              console.log('Comment for pattern1 added to database!');
          });
  res.redirect('/pattern2');
});



//   **************PATERN 3*********
router.get('/pattern3', function (req, res) {
var query = {"patternid": 3};
 Comment.where(query).then(function(comments){
    console.log(comments);
    res.render('pattern3', { title: 'Express', all_comments: comments });
  });
});

router.post('/pattern3', function (req, res) {
  var comment_data = {
                        "lastrating"  : req.body.rating,
                        "patternid"   : req.body.patternid,
                        "namecoment"  : req.body.commenter,
                        "coment"  : req.body.commentBody,
                        "email"   : req.body.email 
                     };
  console.log(comment_data);
  var comment_object = new Comment(comment_data);
  comment_object.save(
          function(err){
            if(err)
              console.log(err);
            else
              console.log('Comment for pattern1 added to database!');
          });
  res.redirect('/pattern3');
});


//   **************PATERN 4*********
router.get('/pattern4', function (req, res) {
var query = {"patternid": 4};
 Comment.where(query).then(function(comments){
    console.log(comments);
    res.render('pattern4', { title: 'Express', all_comments: comments });
  });
});


router.post('/pattern4', function (req, res) {
  var comment_data = {
                        "lastrating"  : req.body.rating,
                        "patternid"   : req.body.patternid,
                        "namecoment"  : req.body.commenter,
                        "coment"  : req.body.commentBody,
                        "email"   : req.body.email 
                     };
  console.log(comment_data);
  var comment_object = new Comment(comment_data);
  comment_object.save(
          function(err){
            if(err)
              console.log(err);
            else
              console.log('Comment for pattern1 added to database!');
          });
  res.redirect('/pattern4');
});


//   **************PATERN 5*********
router.get('/pattern5', function (req, res) {
var query = {"patternid": 5};
 Comment.where(query).then(function(comments){
    console.log(comments);
    res.render('pattern5', { title: 'Express', all_comments: comments });
  });
});
router.post('/pattern5', function (req, res) {
  var comment_data = {
                        "lastrating"  : req.body.rating,
                        "patternid"   : req.body.patternid,
                        "namecoment"  : req.body.commenter,
                        "coment"  : req.body.commentBody,
                        "email"   : req.body.email 
                     };
  console.log(comment_data);
  var comment_object = new Comment(comment_data);
  comment_object.save(
          function(err){
            if(err)
              console.log(err);
            else
              console.log('Comment for pattern1 added to database!');
          });
  res.redirect('/pattern5');
});


//   **************PATERN 6*********
router.get('/pattern6', function (req, res) {
var query = {"patternid": 6};
 Comment.where(query).then(function(comments){
    console.log(comments);
    res.render('pattern6', { title: 'Express', all_comments: comments });
  });
});

router.post('/pattern6', function (req, res) {
  var comment_data = {
                        "lastrating"  : req.body.rating,
                        "patternid"   : req.body.patternid,
                        "namecoment"  : req.body.commenter,
                        "coment"  : req.body.commentBody,
                        "email"   : req.body.email 
                     };
  console.log(comment_data);
  var comment_object = new Comment(comment_data);
  comment_object.save(
          function(err){
            if(err)
              console.log(err);
            else
              console.log('Comment for pattern1 added to database!');
          });
  res.redirect('/pattern6');
});



//   **************PATERN 7 ***************
router.get('/pattern7', function (req, res) {
 var query = {"patternid": 7};
 Comment.where(query).then(function(comments){
    console.log(comments);
    res.render('pattern7', { title: 'Express', all_comments: comments });
  });
});

router.post('/pattern7', function (req, res) {
  var comment_data = {
                        "lastrating"  : req.body.rating,
                        "patternid"   : req.body.patternid,
                        "namecoment"  : req.body.commenter,
                        "coment"  : req.body.commentBody,
                        "email"   : req.body.email 
                     };
  console.log(comment_data);
  var comment_object = new Comment(comment_data);
  comment_object.save(
          function(err){
            if(err)
              console.log(err);
            else
              console.log('Comment for pattern1 added to database!');
          });
  res.redirect('/pattern7');
});


//   **************PATERN 8 ***************
router.get('/pattern8', function (req, res) {
var query = {"patternid": 8};
 Comment.where(query).then(function(comments){
    console.log(comments);
    res.render('pattern8', { title: 'Express', all_comments: comments });
  });
});

router.post('/pattern8', function (req, res) {
  var comment_data = {
                        "lastrating"  : req.body.rating,
                        "patternid"   : req.body.patternid,
                        "namecoment"  : req.body.commenter,
                        "coment"  : req.body.commentBody,
                        "email"   : req.body.email 
                     };
  console.log(comment_data);
  var comment_object = new Comment(comment_data);
  comment_object.save(
          function(err){
            if(err)
              console.log(err);
            else
              console.log('Comment for pattern1 added to database!');
          });
  res.redirect('/pattern8');
});


//   **************PATERN 9 ***************
router.get('/pattern9', function (req, res) {
 var query = {"patternid": 9};
 Comment.where(query).then(function(comments){
    console.log(comments);
    res.render('pattern9', { title: 'Express', all_comments: comments });
  });
});


router.post('/pattern9', function (req, res) {
  var comment_data = {
                        "lastrating"  : req.body.rating,
                        "patternid"   : req.body.patternid,
                        "namecoment"  : req.body.commenter,
                        "coment"  : req.body.commentBody,
                        "email"   : req.body.email 
                     };
  console.log(comment_data);
  var comment_object = new Comment(comment_data);
  comment_object.save(
          function(err){
            if(err)
              console.log(err);
            else
              console.log('Comment for pattern1 added to database!');
          });
  res.redirect('/pattern9');
});





router.get('/survey', function (req, res) {
  res.render('survey', { title: 'Express' });
});

router.get('/profile', function (req, res) {
  res.render('profile', { title: 'Express' });
});

module.exports = router;
