# 瑞吉外卖
1. day6前端文件里main.js第一行ajax请求没有加data，所以在把 cartListApi中的注释改掉之后显示不出分类信息
2. 
>divCartContent
    >divCartItem
        >divNum
            >divDishNum
把font-size改一改

front/index.html下134行
<div class="divDishNum">{{item.number}}</div>
加上style="font-size:16px"
即
<div class="divDishNum" style="font-size:16px">{{item.number}}</div>

3.
redis.windows.conf文件修改
注释掉bind 127.0.0.1
声明port 6379
声明requirepass 123456

4.
不使用mysql主从复制时记得注释掉maven中的sharding依赖，并刷新maven文件

## 逻辑
去结算跳转到/front/page/add-order.html
结果直接跳到了address-edit.html
为什么？
因为没有默认地址
```javascript
//获取默认地址
async defaultAddress(){
    const res = await  getDefaultAddressApi()
    if(res.code === 1){
        this.address = res.data
        this.getFinishTime()
    }else{
        window.requestAnimationFrame(()=>{
            window.location.href = '/front/page/address-edit.html'
        })
    }
},
```

## 关于注解
@RequestMapping用来声明
网页请求映射
可用于类（通常）和方法
@RequestBody用来绑定网页发送的JSON数据到一个java对象(map也可)（只用于POST请求，取出http请求体中的数据）
```java
@PostMapping("/login")
public R<User> login(@RequestBody Map map, HttpSession session)
```
@PathVariable用来将URI中的值注入到方法参数中
@Get/Post/DeleteMapping等是
    @RequestMapping(method=RequestMethod.GET/POST/DELETE)
的shortcut
@RequestParam(只用于POST请求，取出http请求头中的数据)

请求 URL: http://localhost:8080/setmeal/status/0?ids=1415580119015145474,1583260610277715970 请求方法: POST

```java
@GetMapping("/users")
public String getUsers(@RequestParam("name") String userName) {
    // method implementation
}

@GetMapping("/users")
public String getUsers(@RequestParam(value = "name", required = false, defaultValue = "John Doe") String userName) {
    // method implementation
}
```

## 关于传参形式



### Sample1
前端：
请求：http://localhost:8080/order/userPage?page=1&pageSize=1
后端：ordercontroller中声明@RequestMapping("/order/userPage")

### Sample2(斜杠传参)backend菜品管理修改菜品时
请求GET：http://localhost:8080/dish/1413384757047271425
后端：
其中dishcontroller已声明("/dish")
需要声明PathVariable用于接收直接传参 表明在请求头里

```java
@GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id)
```

### Sample3(多个参数)

请求POST/DELETE/UPDATE：http://localhost:8080/setmeal/status/0?ids=1415580119015145474,1583260610277715970
后端：
声明RequestParam表明在请求头里

```java
@DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids)
```

### Sample4(JSON传参)新增套餐时(通常使用实体类接收)



请求POST：http://localhost:8080/setmeal（含载荷JSON）
![alt text](image.png)
后端：
其中setmealcontroller已声明("/setmeal")
需要声明RequestBody用于接收（请求体）JSON数据

```java
@PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto)
```

### Sample5(直接取值赋值)backend点击菜品管理时
前端：
请求：http://localhost:8080/dish/page?page=1&pageSize=10
（载荷中也可以看到page：1   pageSize：10）（载荷payload中确实能看到，难道是浏览器的优化？理论上page和pageSize在请求头中）（GPT说这是query string，不属于requestbody或requestheader）
java程序断点处发现page pageSize都成功赋上了值，name为null
后端：

```java
@GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name)
```