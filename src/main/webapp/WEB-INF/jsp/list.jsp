<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="common/tag.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>列表页</title>
    <%@include file="common/head.jsp" %>
</head>
<body>
<div class="container">
    <div class="panel panel-default">
        <div class="panel-heading text-center">
            <h2>
                秒杀列表
            </h2>
        </div>
        <div class="panel-body">
            <table class="table table-hover">
                <thead>
                <tr>
                    <th>商品名称</th>
                    <th>商品库存</th>
                    <th>秒杀开始时间</th>
                    <th>商品结束时间</th>
                    <th>商品创建时间</th>
                    <th>详情页</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${list }" var="sk">
                    <tr>
                        <td>${sk.name }</td>
                        <td>${sk.number }</td>
                        <td>
                            <fmt:formatDate value="${sk.startTime }" pattern="yyyy-MM-dd HH-mm-ss"/>
                        </td>
                        <td>
                            <fmt:formatDate value="${sk.endTime }" pattern="yyyy-MM-dd HH-mm-ss"/>
                        </td>
                        <td>
                            <fmt:formatDate value="${sk.createTime }" pattern="yyyy-MM-dd HH-mm-ss"/>
                        </td>
                        <td>
                            <a class="btn btn-info" href="/seckill/seckill/${sk.seckillId }/detail">link</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

    </div>

</div>


</body>
<!-- 新 Bootstrap 核心 CSS 文件 -->
<link href="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">

<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/js/bootstrap.min.js"></script>
</html>