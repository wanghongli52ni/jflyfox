<%@page language="java" pageEncoding="UTF-8"%>

<div id="paginator" style="margin: auto;">
<input type="hidden" id="paginator_totalrecords" name="totalRecords" value="${page.totalRow }"/>
<input type="hidden" id="paginator_pageno" name="pageNo" value="${page.pageNumber }"/>
<input type="hidden" id="paginator_recordsperpage" name="pageSize" value="${page.pageSize }" />
<input type="hidden" id="paginator_length" name="length" value="10" />
