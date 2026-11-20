const form=document.querySelector('#form'), rows=document.querySelector('#rows'), message=document.querySelector('#message');
let editing=null;
function show(text,ok){message.textContent=text;message.className=ok?'success':'error';message.hidden=false;}
async function request(path='',options={}){
 const response=await fetch('/api/coworking'+path,{headers:{'Content-Type':'application/json'},...options});
 const data=await response.json();return {ok:response.ok,status:response.status,data};
}
function reset(){editing=null;form.reset();form.elements.id.readOnly=false;document.querySelector('#form-title').textContent='Новий інтервал';document.querySelector('#save').textContent='Додати інтервал';document.querySelector('#cancel').hidden=true;}
function edit(item){editing=item.id;for(const key of ['id','resident','space','start','end'])form.elements[key].value=item[key];form.elements.id.readOnly=true;document.querySelector('#form-title').textContent='Змінити інтервал';document.querySelector('#save').textContent='Зберегти зміни';document.querySelector('#cancel').hidden=false;form.elements.resident.focus();}
async function load(){
 const result=await request();if(!result.ok){show(result.data.message,false);return;}
 rows.replaceChildren();const items=result.data.items;document.querySelector('#count').textContent=items.length+' інтервалів';document.querySelector('#empty').hidden=items.length>0;
 for(const item of items){const row=document.createElement('tr');for(const key of ['id','resident','space','start','end']){const td=document.createElement('td');td.textContent=item[key].replace('T',' ');if(key!=='start'&&key!=='end')td.textContent=item[key];row.append(td);}
 const actions=document.createElement('td'), change=document.createElement('button'), remove=document.createElement('button');change.type=remove.type='button';change.textContent='Змінити';change.className='row-edit';change.addEventListener('click',()=>edit(item));remove.textContent='Видалити';remove.className='row-delete';remove.addEventListener('click',async()=>{try{const result=await request('/'+encodeURIComponent(item.id),{method:'DELETE'});show(result.data.message,result.ok);if(result.ok&&editing===item.id)reset();await load();}catch(e){show('Не вдалося зв’язатися із сервером.',false);}});actions.append(change,document.createElement('br'),remove);row.append(actions);rows.append(row);}
}
form.addEventListener('submit',async event=>{event.preventDefault();const save=document.querySelector('#save');save.disabled=true;try{const body=Object.fromEntries(new FormData(form));const result=await request(editing?'/'+encodeURIComponent(editing):'',{method:editing?'PUT':'POST',body:JSON.stringify(body)});show(result.data.message,result.ok);if(result.ok)reset();await load();}catch(e){show('Не вдалося зв’язатися із сервером.',false);}finally{save.disabled=false;}});
document.querySelector('#cancel').addEventListener('click',reset);document.querySelector('#refresh').addEventListener('click',()=>load().catch(()=>show('Не вдалося зв’язатися із сервером.',false)));load().catch(()=>show('Не вдалося зв’язатися із сервером.',false));
