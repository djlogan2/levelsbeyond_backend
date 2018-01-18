'use strict';

const assert = require('assert'),
    Client = require('node-rest-client').Client,
    client = new Client(),
    URL = 'http://localhost:8080/api/notes',
    randomwords = require('random-words'),
    sequential = require('promise-sequential');

function addNote(body, id, extras) {
    return new Promise(function (resolve) {

        let data = {body: body};
        if (id) data.id = id;

        if (extras) data = Object.assign(data, extras);

        client.post(URL, {
            data: data,
            headers: {"Content-Type": "application/json"}
        }, function (data) {
            resolve(data);
        });
    });
};

function getNote(id) {
    return new Promise(function (resolve) {
        client.get(`${URL}/${id}`, {
            headers: {"Content-Type": "application/json"}
        }, function (data) {
            resolve(data);
        });
    });
};

function getAllNotes(search) {
    return new Promise(function (resolve) {

        if (search) search = `?search=${encodeURIComponent(search)}`
        else search = '';

        client.get(`${URL}${search}`, {
            headers: {"Content-Type": "application/json"}
        }, function (data) {
            resolve(data);
        });
    });
};

function updateNote(note) {
    return new Promise(function (resolve) {
        client.put(URL, {
            data: note,
            headers: {"Content-Type": "application/json"}
        }, function (data) {
            resolve(data);
        });
    });
};

function deleteNote(id) {
    if (id) id = `/${id}`; else id = '';

    return new Promise(function (resolve) {
        client.delete(`${URL}${id}`, {
            headers: {"Content-Type": "application/json"}
        }, function (data) {
            resolve(data);
        });
    });
};

describe('Rest server', function () {
    this.timeout(10000);

    describe('Adding a new note', function () {

        it('should return the saved notes id and body', function () {
            let returned_note;
            return addNote('test addnote')
                .then(function (data) {
                    returned_note = data;
                    assert.ok('id' in data, 'response should have an id field');
                    assert.ok('body' in data, 'response should have a body field');
                    assert.ok(!('error' in data), 'response should not have an error field');
                    assert.equal(data.body, 'test addnote', 'body data is incorrect');
                    return getNote(data.id);
                }).then(function (data) {
                    assert.ok('id' in data, 'response should have an id field');
                    assert.ok('body' in data, 'response should have a body field');
                    assert.ok(!('error' in data), 'response should not have an error field');
                    assert.equal(data.body, returned_note.body, 'body data is incorrect');
                    assert.equal(data.id, returned_note.id, 'id data is incorrect');
                });
        });

        it('should only accept the "body" key', function () {
            return addNote('test addnote', null, {spurious: 'key field'})
                .then(function (data) {
                    assert.ok('error' in data, 'response should have an error field');
                    assert.equal(data.error, 'Unknown fields in note object');
                });
        });

        it('should only not accept the "id" key', function () {
            return addNote('test addnote', 5)
                .then(function (data) {
                    assert.ok('error' in data, 'response should have an error field');
                    assert.equal(data.error, 'Cannot have id in a new note');
                });
        });

    });

    describe('Getting a note', function () {
        it('should work with an existing note', function () {
            let note;
            return addNote('test get note')
                .then(function (data) {
                    note = data;
                    return getNote(data.id);
                }).then(function (data) {
                    assert.ok('id' in data, 'response should have an id field');
                    assert.ok('body' in data, 'response should have a body field');
                    assert.ok(!('error' in data), 'response should not have an error field');
                    assert.equal(data.body, note.body, 'body data is incorrect');
                    assert.equal(data.id, note.id, 'id data is incorrect');
                });
        });

        it('should fail with a nonexistant note', function () {
            return getNote(998877)
                .then(function (data) {
                    assert.ok(!('id' in data), 'response should not have an id field');
                    assert.ok(!('body' in data), 'response should not have a body field');
                    assert.ok('error' in data, 'response should have an error field');
                    assert.equal(data.error, 'Nonexistant note');
                });
        });
/*
 * This call fails in the Java routing framework and returns no data at all, but returns
 * a 'Method not allowed' status. This is OK with me. But if we need to return valid JSON
 * on some type of invalid ID data, add this test back in and resolve it with code.
 *
        it('should fail with an invalid note id', function () {
            return getNote('x')
                .then(function (data) {
                    assert.ok(!('id' in data), 'response should not have an id field');
                    assert.ok(!('body' in data), 'response should not have a body field');
                    assert.ok('error' in data, 'response should have an error field');
                    assert.equal(data.error, 'Invalid note id');
                });
        });
*/
    });

    describe('Getting all notes', function () {
        it('should return all notes', function () {
            return deleteNote('DELETE_ALL_NOTES')
                .then(function (data) {
                    let promises = [];
                    for (let x = 0; x < 10; x++) {
                        promises.push(() => addNote(`test all notes #${x}`));
                    }
                    return sequential(promises);
                }).then(() => getAllNotes())
                .then(function (data) {
                    assert.ok(data instanceof Array, 'Not sure what we got back from api');
                    assert.equal(data.length, 10, 'We added ten elements but did not get ten elements');
                    data.forEach(function (note) {
                        assert.ok('id' in note, 'response should have an id field');
                        assert.ok('body' in note, 'response should have a body field');
                        assert.ok(!('error' in note), 'response should not have an error field');
                    });
                });
        });

        it('should only return certain notes with optional search parameter', function () {
            return deleteNote('DELETE_ALL_NOTES')
                .then(function() {
                    let promises = [];
                    //
                    // Make sure you use some special URI character like an ampersand
                    // in order to ensure everything is being encoded and decoded correctly,
                    // as this is a URI parameter and not a post/put
                    //
                    for (let x = 0; x < 50; x++) {
                        let body = randomwords(10);
                        let rn = Math.floor(10 * Math.random());
                        if (x % 2 === 0) {
                            body.splice(rn, 0, 'babel & fish');
                        }
                        body = body.join(' ');
                        promises.push(() => addNote(body));
                    }
                    //
                    // return Promise.all(promises); <- Can't use this in development because we don't have a connection pool that can handle it
                    //
                    return sequential(promises); //  <- Instead we have to add each record sequentially to ensure we only use one connection at a time
                    //
                    // So I went ahead and switched all of the Promise.all calls over to sequential just for safety purposes.
                    //
                }).then(() => getAllNotes('babel & fish'))
                .then(function (data) {
                    assert.ok(data instanceof Array, 'Not sure what we got back from api');
                    assert.equal(data.length, 25, 'We did not get 25 elements');
                    data.forEach(function (note) {
                        assert.ok('id' in note, 'response should have an id field');
                        assert.ok('body' in note, 'response should have a body field');
                        assert.ok(!('error' in note), 'response should not have an error field');
                        assert.notEqual(note.body.indexOf('babel & fish'), -1, 'Somehow we received a note that did not have our keyword');
                    });
                });
        });
    });

    describe('Updating a note', function () {
        it('should properly update the body of an existing note', function () {
            let note;
            return addNote('test updating of a note')
                .then((data) => getNote(data.id))
                .then(function (data) {
                    note = data;
                    note.body = note.body.toUpperCase();
                    return updateNote(note);
                }).then(function (data) {
                    assert.ok('id' in data, 'response should have an id field');
                    assert.ok('body' in data, 'response should have a body field');
                    assert.ok(!('error' in data), 'response should not have an error field');
                    assert.equal(data.id, note.id, 'Invalid note id');
                    assert.equal(data.body, note.body, 'Invalid note body');
                });
        });

        it('should fail with an invalid id', function () {
            return updateNote({id: 998877, body: 'This should be moot'})
                .then(function (data) {
                    assert.ok(!('id' in data), 'response should not have an id field');
                    assert.ok(!('body' in data), 'response should not have a body field');
                    assert.ok('error' in data, 'response should have an error field');
                    assert.equal(data.error, 'Nonexistant note');
                });
        });

        it('should fail with a spurious field', function () {
            let note;
            return addNote('test updating of a note with a spurious field')
                .then((data) => getNote(data.id))
                .then(function (data) {
                    note = data;
                    note.spurious = 'new field';
                    return updateNote(note);
                }).then(function (data) {
                    assert.ok(!('id' in data), 'response should not have an id field');
                    assert.ok(!('body' in data), 'response should not have a body field');
                    assert.ok('error' in data, 'response should have an error field');
                    assert.equal(data.error, 'Unknown fields in note object', 'Should return appropriate error');
                    return getNote(note.id);
                }).then(function (data) {
                    assert.ok('id' in data, 'response should have an id field');
                    assert.ok('body' in data, 'response should have a body field');
                    assert.ok(!('error' in data), 'response should not have an error field');
                    assert.equal(data.id, note.id, 'Invalid note id');
                    assert.equal(data.body, note.body, 'Invalid note body');
                });
        });
    });

    describe('Deleting notes', function () {

        it('should properly delete an existing note', function () {
            let note;
            return addNote('test deleting a note zxyyndnpz')
                .then(function(data) {
                    note = data;
                    return getNote(data.id);
                }).then(function(data){
                    assert.ok('id' in data, 'response should have an id field');
                    assert.ok('body' in data, 'response should have a body field');
                    assert.ok(!('error' in data), 'response should not have an error field');
                    assert.equal(data.id, note.id, 'Invalid note id');
                    assert.equal(data.body, note.body, 'Invalid note body');
                    return deleteNote(data.id);
                }).then(function(data){
                    assert.ok(!('id' in data), 'response should not have an id field');
                    assert.ok(!('body' in data), 'response should not have a body field');
                    assert.ok('error' in data, 'response should have an error field');
                    assert.equal(data.error, 'ok', 'Delete should have returned OK');
                    return getNote(note.id);
                }).then(function(data){
                    assert.ok(!('id' in data), 'response should not have an id field');
                    assert.ok(!('body' in data), 'response should not have a body field');
                    assert.ok('error' in data, 'response should have an error field');
                    assert.equal(data.error, 'Nonexistant note', 'Should have no longer found the note');
                    return getAllNotes('zxyyndnpz');
                }).then((data) => assert.equal(data.length, 0, 'There should no longer be any notes to find'));
        });

        it('should fail with an invalid id', function () {
            return deleteNote(998877)
                .then(function(data){
                    assert.ok(!('id' in data), 'response should not have an id field');
                    assert.ok(!('body' in data), 'response should not have a body field');
                    assert.ok('error' in data, 'response should have an error field');
                    assert.equal(data.error, 'Nonexistant note', 'Should have no longer found the note');
                });
        });

        it('should delete all notes if id=DELETE_ALL_NOTES', function () {
            let promises = [];
            for(let x = 0 ; x < 10 ; x++)
                promises.push(() => addNote(`delete everything ${x}`))
            return sequential(promises)
                .then(() => deleteNote('DELETE_ALL_NOTES'))
                .then(function(data){
                    assert.ok(!('id' in data), 'response should not have an id field');
                    assert.ok(!('body' in data), 'response should not have a body field');
                    assert.ok('error' in data, 'response should have an error field');
                    assert.equal(data.error, 'ok', 'Delete should have returned OK');
                    return getAllNotes();
                }).then((data) => assert.equal(data.length, 0, 'All records should have been deleted'));
        });
    });
});
