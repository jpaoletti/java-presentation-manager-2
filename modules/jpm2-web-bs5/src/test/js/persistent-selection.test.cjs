// Run with: node --test modules/jpm2-web-bs5/src/test/js/persistent-selection.test.cjs
const {test} = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');
const source = fs.readFileSync(path.resolve(__dirname, '../../main/webapp/static/js/jpm.js'), 'utf8');
const helpers = source.slice(source.indexOf('// IDs only; sessionStorage'), source.indexOf('var initPage = function'));

// Small DOM adapter for exercising the actual handlers without browser dependencies.
function element(attrs = {}) {
    return {attrs, handlers: {}, children: {}, checked: false};
}
class Nodes extends Array {
    attr(name, value) {
        if (value === undefined) return this[0]?.attrs[name];
        this.forEach(n => n.attrs[name] = value);
        return this;
    }
    prop(name, value) { this.forEach(n => n[name] = value); return this; }
    text(value) { this.forEach(n => n.text = value); return this; }
    each(fn) { this.forEach((n, i) => fn.call(n, i, n)); return this; }
    find(selector) { return wrap(this.flatMap(n => typeof n.children[selector] === 'function' ? n.children[selector]() : n.children[selector] || [])); }
    filter(selector) { return typeof selector === 'function' ? super.filter(selector) : wrap(Array.from(this).filter(n => n.checked)); }
    off(event) { this.forEach(n => delete n.handlers[event]); return this; }
    on(event, fn) { this.forEach(n => n.handlers[event] = fn); return this; }
    closest() { return wrap(this.map(n => n.parent)); }
    remove() { this.forEach(n => n.removed = true); return this; }
    map(fn) { return typeof fn === 'function' && fn.length === 0 ? wrap(Array.from(this, n => fn.call(n))) : super.map(fn); }
    get() { return Array.from(this); }
    first() { return wrap([this[0]]); }
}
function wrap(nodes) { return Nodes.from(nodes.filter(Boolean)); }
function fire(node, event, checked) {
    if (checked !== undefined) node.checked = checked;
    node.handlers[event].call(node);
}
function setup(storageBlocked = false) {
    const data = new Map();
    let tables = [], review;
    const navigations = [], posts = [];
    const context = {
        URL, currentUser: 'ana', getContextPath: () => 'http://localhost/trekker/',
        window: {location: {href: 'http://localhost/trekker/jpm/orden/list?page=1', origin: 'http://localhost'}, history: {back() {}}},
        sessionStorage: {
            getItem: k => { if (storageBlocked) throw Error('disabled'); return data.get(k) || null; },
            setItem: (k,v) => { if (storageBlocked) throw Error('disabled'); data.set(k,v); },
            removeItem: k => { if (storageBlocked) throw Error('disabled'); data.delete(k); }
        },
        $: value => {
            if (typeof value !== 'string') return wrap([value]);
            if (value.startsWith('<form')) return wrap([element()]);
            if (value === 'body') return {append() {}};
            if (value === '#jpmForm') return wrap([]);
            if (value === '#jpm-selection-review') return wrap([review]);
            return wrap(tables);
        },
        jpmNavigate: url => navigations.push(url),
        buildAjaxJpmFormObject: (form, callback) => ({submit: () => posts.push({url: form.attr('action'), callback})}),
        processFormResponse() {}
    };
    vm.createContext(context);
    vm.runInContext(helpers, context);
    return {context, data, navigations, posts, setTables: t => tables=t, setReview: r => review=r};
}
function page(ids, owner = '') {
    const table = element({'data-selection-entity':'orden', 'data-selection-owner':'cliente', 'data-selection-owner-id':owner});
    const checks = ids.map(id => element({'data-id':id}));
    const button = element({href:'http://localhost/trekker/jpm/orden/@@/agrupar.exec'});
    const count = element(), clear = element(), review = element(), header = element(), toolbar = element();
    table.children = {'.selectable':checks, '.selected-operation[data-selection-mode="persistent"]':[button], '.jpm-selection-toolbar':[toolbar], '.jpm-selection-count':[count], '#select_unselect_all':[header], '.jpm-selection-clear':[clear], '.jpm-selection-review':[review]};
    return {table, checks, button, count, clear, review, header};
}
function reviewPage(ids, confirm = false, unavailable = []) {
    const review = element({'data-confirm':String(confirm)});
    const rows = ids.map(id => element({'data-selection-id':id}));
    const removes = rows.map(row => Object.assign(element(), {parent:row}));
    const next = element(), cancel = element(), count = element();
    review.children = {'tr[data-selection-id]': () => rows.filter(r => !r.removed), '[data-unavailable="true"]': () => rows.filter(r => !r.removed && unavailable.includes(r.attrs['data-selection-id'])), '.jpm-selection-remove':removes, '.jpm-selection-count':[count], '.jpm-selection-continue':[next], '.jpm-selection-cancel':[cancel]};
    return {review, removes, next, cancel, count};
}
function selection(env, table) { return Array.from(env.context.jpmSelectionRead(env.context.jpmSelectionKey(wrap([table])))); }

test('accumulate across pages, deduplicate, restore and deselect', () => {
    const env = setup(), a = page(['1','2']), b = page(['2','3']);
    env.setTables([a.table]); env.context.initPersistentSelection();
    fire(a.checks[0], 'change.jpmSelection', true);
    fire(a.checks[1], 'change.jpmSelection', true);
    env.setTables([b.table]); env.context.initPersistentSelection();
    assert.equal(b.checks[0].checked, true);
    fire(b.checks[1], 'change.jpmSelection', true);
    fire(b.checks[1], 'change.jpmSelection', true);
    assert.deepEqual(selection(env,b.table), ['1','2','3']);
    fire(b.checks[0], 'change.jpmSelection', false);
    env.setTables([a.table]); env.context.initPersistentSelection();
    assert.equal(a.checks[0].checked, true);
    assert.equal(a.checks[1].checked, false);
    assert.equal(a.count.text, 2);
    assert.equal(a.header.indeterminate, true);
});
test('select all on a page preserves other pages; clear resets all', () => {
    const env = setup(), a = page(['1']), b = page(['2','3']);
    env.setTables([a.table]); env.context.initPersistentSelection(); fire(a.checks[0], 'change.jpmSelection', true);
    env.setTables([b.table]); env.context.initPersistentSelection();
    b.checks.forEach(c => c.checked = true); b.checks.forEach(c => fire(c, 'change.jpmSelection'));
    assert.deepEqual(selection(env,b.table), ['1','2','3']);
    b.checks.forEach(c => c.checked = false); b.checks.forEach(c => fire(c, 'change.jpmSelection'));
    assert.deepEqual(selection(env,b.table), ['1']);
    fire(b.clear, 'click.jpmSelection');
    assert.deepEqual(selection(env,b.table), []);
    assert.equal(b.count.text, 0);
});
test('isolate by user, owner and entity; reload from session storage', () => {
    const env = setup(), a = page(['1'], '100'), b = page(['2'], '200');
    env.setTables([a.table]); env.context.initPersistentSelection(); fire(a.checks[0], 'change.jpmSelection', true);
    assert.deepEqual(selection(env,b.table), []);
    env.context.currentUser = 'bea'; assert.deepEqual(selection(env,a.table), []);
    env.context.currentUser = 'ana';
    a.table.attrs['data-selection-entity']='other'; assert.deepEqual(selection(env,a.table), []);
    a.table.attrs['data-selection-entity']='orden';
    env.context.jpmSelectionMemory = Object.create(null);
    assert.deepEqual(selection(env,a.table), ['1']);
});
test('disabled storage still preserves selections during partial navigation', () => {
    const env = setup(true), a = page(['1']);
    env.setTables([a.table]); env.context.initPersistentSelection(); fire(a.checks[0], 'change.jpmSelection', true);
    assert.deepEqual(selection(env,a.table), ['1']);
});
test('review removes rows, retains cancellation and opens executor with remaining IDs', () => {
    const env = setup(), a = page(['1','2']);
    env.setTables([a.table]); env.context.initPersistentSelection(); a.checks.forEach(c => fire(c,'change.jpmSelection',true));
    fire(a.review,'click.jpmSelection');
    const url = new URL(env.navigations[0]);
    assert.equal(url.searchParams.get('selectionReview'), 'true');
    assert.equal(url.pathname, '/trekker/jpm/orden/1,2/agrupar.exec');
    env.context.window.location.href=url.href;
    const r = reviewPage(['1','2']); env.setTables([]); env.setReview(r.review); env.context.initPersistentSelection();
    fire(r.removes[0],'click.jpmSelection');
    assert.deepEqual(selection(env,a.table), ['2']);
    fire(r.cancel,'click.jpmSelection');
    assert.equal(env.navigations.at(-1), 'http://localhost/trekker/jpm/orden/list?page=1');
    assert.deepEqual(selection(env,a.table), ['2']);
    fire(r.next,'click.jpmSelection');
    const next = new URL(env.navigations.at(-1));
    assert.equal(next.pathname, '/trekker/jpm/orden/2/agrupar.exec');
    assert.equal(next.searchParams.has('selectionReview'), false);
    assert.ok(next.searchParams.has('_jpmSelectionKey'));
});
test('unavailable rows and empty selection block continuation; confirmation uses POST', () => {
    const env = setup(); env.context.window.location.href='http://localhost/trekker/jpm/orden/1,2/agrupar.exec?selectionReview=true';
    const r = reviewPage(['1','2'], true, ['1']); env.setReview(r.review); env.context.initPersistentSelection();
    assert.equal(r.next.disabled,true); fire(r.next,'click.jpmSelection'); assert.equal(env.posts.length,0);
    fire(r.removes[0],'click.jpmSelection'); assert.equal(r.next.disabled,false);
    fire(r.next,'click.jpmSelection'); assert.equal(env.posts.length,1);
    assert.equal(new URL(env.posts[0].url).pathname,'/trekker/jpm/orden/2/agrupar.exec');
    fire(r.removes[1],'click.jpmSelection'); assert.equal(r.next.disabled,true);
});
test('only successful execution clears the corresponding selection', () => {
    const env=setup(), a=page(['1']), b=page(['2'],'other');
    env.setTables([a.table,b.table]); env.context.initPersistentSelection();
    fire(a.checks[0],'change.jpmSelection',true); fire(b.checks[0],'change.jpmSelection',true);
    const url=new URL('http://localhost/trekker/jpm/orden/1/agrupar.exec');
    url.searchParams.set('_jpmSelectionKey',env.context.jpmSelectionKey(wrap([a.table])));
    env.context.jpmSelectionSucceeded({ok:false},url.href); assert.deepEqual(selection(env,a.table),['1']);
    env.context.jpmSelectionSucceeded({ok:true},url.href); assert.deepEqual(selection(env,a.table),[]);
    assert.deepEqual(selection(env,b.table),['2']);
});
test('explicit form actions clear only the current operation, and malformed storage is ignored', () => {
    const env=setup(), a=page(['1']);
    const key=env.context.jpmSelectionKey(wrap([a.table]));
    env.data.set(key,'broken json'); assert.deepEqual(selection(env,a.table),[]);
    env.data.set(key,JSON.stringify(['1','1',null,42])); assert.deepEqual(selection(env,a.table),['1']);
    env.context.jpmSelectionWrite(key,['1']);
    const current=new URL('http://localhost/trekker/jpm/orden/1/agrupar.exec');
    current.searchParams.set('_jpmSelectionKey',key); env.context.window.location.href=current.href;
    env.context.jpmSelectionSucceeded({ok:true},'http://localhost/trekker/jpm/orden/search');
    assert.deepEqual(selection(env,a.table),['1']);
    env.context.jpmSelectionSucceeded({ok:true},current.origin+current.pathname);
    assert.deepEqual(selection(env,a.table),[]);
});

test('browser Back or reload never restores removed rows or a completed selection', () => {
    const env=setup(), a=page(['1','2']);
    env.setTables([a.table]); env.context.initPersistentSelection();
    a.checks.forEach(c => fire(c,'change.jpmSelection',true));
    fire(a.review,'click.jpmSelection');
    env.context.window.location.href=env.navigations[0]; env.setTables([]);
    let r=reviewPage(['1','2']); env.setReview(r.review); env.context.initPersistentSelection();
    fire(r.removes[0],'click.jpmSelection');
    r=reviewPage(['1','2']); env.setReview(r.review); env.context.initPersistentSelection();
    assert.equal(r.count.text,1);
    assert.deepEqual(selection(env,a.table),['2']);
    fire(r.next,'click.jpmSelection');
    env.context.jpmSelectionSucceeded({ok:true},env.navigations.at(-1));
    env.context.jpmSelectionMemory=Object.create(null);
    r=reviewPage(['1','2']); env.setReview(r.review); env.context.initPersistentSelection();
    assert.equal(r.count.text,0);
    assert.equal(r.next.disabled,true);
    assert.deepEqual(selection(env,a.table),[]);
});
