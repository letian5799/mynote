# redux 学习笔记

## redux flow

1. 用户发出action  store.dispatch(action)
2. store自动调用reducer,并传入两个参数（当前state,action）,得到返回的新的state
3. state一旦有变化，就调用监听函数 store.subscribe(listener)，其中listener是个函数，React在数据改变时，会重新渲染view

！[图解（补）]()

## 了解Redux架构的作用

> Redux不是非用不可，使用redux会增加项目的复杂度。
> redux的使用场景：多交互，多数据源。

从react组件的角度分析的话，redux在出现以下情况时，是比较适合的。
> - 某个组件的状态需要共享
> - 某个状态需要在任何地方都能拿到
> - 一个组件想改变全局状态
> - 一个组件需要改变另一个组件的状态

## redux的设计思想

>(1) web应用是一个状态机，一个view对应一个状态
(2)所有的状态保存在一个对象中。

## Redux的核心概念和API

### Action

action是一个事件的描述，是改变数据的唯一方法，是view发出的通知，表示数据将要发生变化。

Action 是一个对象。其中的type属性是必须的，表示 Action 的名称。其他属性可以自由设置，社区有一个规范可以参考。
```javascript
const action = {
  type: 'ADD_TODO',
  payload: 'Learn Redux'
};
```
### 创建action函数

创建action函数和action没有关系，，它只是用来生成ction的一个函数，方便产生同一种类型事件的描述
```javascript
const ADD_TODO = '添加 TODO';

function addTodo(text) {
  return {
    type: ADD_TODO,
    text
  }
}

const action = addTodo('Learn Redux');
```
### Reducer

Reducer是一个纯函数，它接受 Action 和当前 State 作为参数，返回一个新的 State。
在实际应用中，Reducer函数是由`store.dispatch(action)`方法自动触发的。
```javascript
const defaultState = 0;
const reducer = (state = defaultState, action) => {
  switch (action.type) {
    case 'ADD':
      return state + action.payload;
    default: 
      return state;
  }
};

import { createStore } from 'redux';
const store = createStore(reducer);
store.dispatch(action)
```
>补充：纯函数，只要相同输入，必然得到相同的输出，其遵守的约束如下：
> - 不得改写参数
  - 不能调用系统 I/O 的API
  - 不能调用Date.now()或者Math.random()等不纯的方法，因为每次会得到不一样的结果

### Reduce的拆分

对于一个大型应用来说，我们把不同类型的reduce函数放在一起显然是不合适的，我们可以根据改变属性的不同，将reducer函数拆分，让每个reducer负责生成对应的属性。这样的话，一个reducer就可以对应一个组件，而且代码结构也更清晰了，便于阅读理解。
Redux提供了`combineReducers`方法可以将不同的reducer再合并成一个大的reducer。

下面通过简单的例子对比拆分前和拆分后的代码。
```javascript
// 拆分前
const chatReducer = (state = defaultState, action = {}) => {
  const { type, payload } = action;
  switch (type) {
    case ADD_CHAT:
      return Object.assign({}, state, {
        chatLog: state.chatLog.concat(payload)
      });
    case CHANGE_STATUS:
      return Object.assign({}, state, {
        statusMessage: payload
      });
    case CHANGE_USERNAME:
      return Object.assign({}, state, {
        userName: payload
      });
    default: return state;
  }
};
```

```javascript
// 拆分后
const chatReducer = (state = defaultState, action = {}) => {
  return {
    chatLog: chatLog(state.chatLog, action),
    statusMessage: statusMessage(state.statusMessage, action),
    userName: userName(state.userName, action)
  }
};

//合并
import { combineReducers } from 'redux';

const chatReducer = combineReducers({
  chatLog,
  statusMessage,
  userName
})
```

总之，combineReducers()做的就是产生一个整体的 Reducer 函数。该函数根据 State 的 key 去执行相应的子 Reducer，并将返回结果合并成一个大的 State 对象。

**附上combineReducers源码的简单实现：**

```javascript
const combineReducers = reducers => {
  return (state = {}, action) => {
    return Object.keys(reducers).reduce(
      (nextState, key) => {
        nextState[key] = reducers[key](state[key], action);
        return nextState;
      },
      {} 
    );
  };
};
```

### store

一个应用只能有一个store，store是保存数据的地方，可以理解为仓库。Redux提供了`createStore(Reducer)`方法来生成store，而store有三个方法：
> `store.getState()` --获取时点状态数据
> `store.dispatch(action)` --view发出action的唯一方法
> `store.subscribe(listener)` --设置监听函数，一旦state发生变化，自动执行这个函数

下面是createStore的简单源码实现
```javascript
const createStore = (reducer)=>{
	let state
	let listeners = []

	const getStore = ()=>{state}

	const dispatch = (action)=>{
		state = reducer(state,action)
		listeners.forEach(listener => listener())
	}

	const subscribe = (lisener)=>{
		listeners.push(listener)
		return listeners = listeners.filter(item => item !=listener)
	}

	dispatch({})

	return {getStore,dispatch,subscribe}
}
```

### state

state是某个时点下store的快照（拷贝）
当前时刻的state，可以通过store.getState()方法获取。
```javascript
import { createStore } from 'redux';
const store = createStore(fn);

const state = store.getState();
```
Redux 规定， 一个 State 对应一个 View。只要 State 相同，View 就相同。你知道 State，就知道 View 是什么样，反之亦然。





