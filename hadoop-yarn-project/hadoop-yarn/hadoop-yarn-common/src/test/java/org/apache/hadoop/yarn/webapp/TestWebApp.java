begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
operator|.
name|StringHelper
operator|.
name|join
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|view
operator|.
name|JQueryUI
operator|.
name|C_TABLE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|view
operator|.
name|JQueryUI
operator|.
name|DATATABLES
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|view
operator|.
name|JQueryUI
operator|.
name|DATATABLES_ID
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|view
operator|.
name|JQueryUI
operator|.
name|_INFO_WRAP
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|view
operator|.
name|JQueryUI
operator|.
name|_TH
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|view
operator|.
name|JQueryUI
operator|.
name|initID
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|view
operator|.
name|JQueryUI
operator|.
name|tableInit
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|GET
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|Produces
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|ext
operator|.
name|ContextResolver
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|ext
operator|.
name|Provider
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|JAXBContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|ArrayUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|MockApps
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|view
operator|.
name|HtmlPage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|view
operator|.
name|JQueryUI
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|view
operator|.
name|TextPage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Singleton
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|json
operator|.
name|JSONConfiguration
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|json
operator|.
name|JSONJAXBContext
import|;
end_import

begin_class
DECL|class|TestWebApp
specifier|public
class|class
name|TestWebApp
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestWebApp
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|FooController
specifier|static
class|class
name|FooController
extends|extends
name|Controller
block|{
DECL|field|test
specifier|final
name|TestWebApp
name|test
decl_stmt|;
DECL|method|FooController (TestWebApp test)
annotation|@
name|Inject
name|FooController
parameter_list|(
name|TestWebApp
name|test
parameter_list|)
block|{
name|this
operator|.
name|test
operator|=
name|test
expr_stmt|;
block|}
DECL|method|index ()
annotation|@
name|Override
specifier|public
name|void
name|index
parameter_list|()
block|{
name|set
argument_list|(
literal|"key"
argument_list|,
name|test
operator|.
name|echo
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|bar ()
specifier|public
name|void
name|bar
parameter_list|()
block|{
name|set
argument_list|(
literal|"key"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
block|}
DECL|method|names ()
specifier|public
name|void
name|names
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|20
condition|;
operator|++
name|i
control|)
block|{
name|renderText
argument_list|(
name|MockApps
operator|.
name|newAppName
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|ex ()
specifier|public
name|void
name|ex
parameter_list|()
block|{
name|boolean
name|err
init|=
name|$
argument_list|(
literal|"clear"
argument_list|)
operator|.
name|isEmpty
argument_list|()
decl_stmt|;
name|renderText
argument_list|(
name|err
condition|?
literal|"Should redirect to an error page."
else|:
literal|"No error!"
argument_list|)
expr_stmt|;
if|if
condition|(
name|err
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"exception test"
argument_list|)
throw|;
block|}
block|}
DECL|method|tables ()
specifier|public
name|void
name|tables
parameter_list|()
block|{
name|render
argument_list|(
name|TablesView
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|FooView
specifier|static
class|class
name|FooView
extends|extends
name|TextPage
block|{
DECL|method|render ()
annotation|@
name|Override
specifier|public
name|void
name|render
parameter_list|()
block|{
name|puts
argument_list|(
name|$
argument_list|(
literal|"key"
argument_list|)
argument_list|,
name|$
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|DefaultController
specifier|static
class|class
name|DefaultController
extends|extends
name|Controller
block|{
DECL|method|index ()
annotation|@
name|Override
specifier|public
name|void
name|index
parameter_list|()
block|{
name|set
argument_list|(
literal|"key"
argument_list|,
literal|"default"
argument_list|)
expr_stmt|;
name|render
argument_list|(
name|FooView
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|TablesView
specifier|static
class|class
name|TablesView
extends|extends
name|HtmlPage
block|{
annotation|@
name|Override
DECL|method|render (Page.HTML<_> html)
specifier|public
name|void
name|render
parameter_list|(
name|Page
operator|.
name|HTML
argument_list|<
name|_
argument_list|>
name|html
parameter_list|)
block|{
name|set
argument_list|(
name|DATATABLES_ID
argument_list|,
literal|"t1 t2 t3 t4"
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|initID
argument_list|(
name|DATATABLES
argument_list|,
literal|"t1"
argument_list|)
argument_list|,
name|tableInit
argument_list|()
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|initID
argument_list|(
name|DATATABLES
argument_list|,
literal|"t2"
argument_list|)
argument_list|,
name|join
argument_list|(
literal|"{bJQueryUI:true, sDom:'t',"
argument_list|,
literal|"aoColumns:[null, {bSortable:false, bSearchable:false}]}"
argument_list|)
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|initID
argument_list|(
name|DATATABLES
argument_list|,
literal|"t3"
argument_list|)
argument_list|,
literal|"{bJQueryUI:true, sDom:'t'}"
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|initID
argument_list|(
name|DATATABLES
argument_list|,
literal|"t4"
argument_list|)
argument_list|,
literal|"{bJQueryUI:true, sDom:'t'}"
argument_list|)
expr_stmt|;
name|html
operator|.
name|title
argument_list|(
literal|"Test DataTables"
argument_list|)
operator|.
name|link
argument_list|(
literal|"/static/yarn.css"
argument_list|)
operator|.
name|_
argument_list|(
name|JQueryUI
operator|.
name|class
argument_list|)
operator|.
name|style
argument_list|(
literal|".wrapper { padding: 1em }"
argument_list|,
literal|".wrapper h2 { margin: 0.5em 0 }"
argument_list|,
literal|".dataTables_wrapper { min-height: 1em }"
argument_list|)
operator|.
name|div
argument_list|(
literal|".wrapper"
argument_list|)
operator|.
name|h2
argument_list|(
literal|"Default table init"
argument_list|)
operator|.
name|table
argument_list|(
literal|"#t1"
argument_list|)
operator|.
name|thead
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|(
literal|"Column1"
argument_list|)
operator|.
name|th
argument_list|(
literal|"Column2"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|tbody
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
literal|"c1r1"
argument_list|)
operator|.
name|td
argument_list|(
literal|"c2r1"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
literal|"c1r2"
argument_list|)
operator|.
name|td
argument_list|(
literal|"c2r2"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|h2
argument_list|(
literal|"Nested tables"
argument_list|)
operator|.
name|div
argument_list|(
name|_INFO_WRAP
argument_list|)
operator|.
name|table
argument_list|(
literal|"#t2"
argument_list|)
operator|.
name|thead
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Column1"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Column2"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|tbody
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
literal|"r1"
argument_list|)
operator|.
comment|// th wouldn't work as of dt 1.7.5
name|td
argument_list|()
operator|.
name|$class
argument_list|(
name|C_TABLE
argument_list|)
operator|.
name|table
argument_list|(
literal|"#t3"
argument_list|)
operator|.
name|thead
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|(
literal|"SubColumn1"
argument_list|)
operator|.
name|th
argument_list|(
literal|"SubColumn2"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|tbody
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
literal|"subc1r1"
argument_list|)
operator|.
name|td
argument_list|(
literal|"subc2r1"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
literal|"subc1r2"
argument_list|)
operator|.
name|td
argument_list|(
literal|"subc2r2"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
literal|"r2"
argument_list|)
operator|.
comment|// ditto
name|td
argument_list|()
operator|.
name|$class
argument_list|(
name|C_TABLE
argument_list|)
operator|.
name|table
argument_list|(
literal|"#t4"
argument_list|)
operator|.
name|thead
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|(
literal|"SubColumn1"
argument_list|)
operator|.
name|th
argument_list|(
literal|"SubColumn2"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|tbody
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
literal|"subc1r1"
argument_list|)
operator|.
name|td
argument_list|(
literal|"subc2r1"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
literal|"subc1r2"
argument_list|)
operator|.
name|td
argument_list|(
literal|"subc2r2"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|echo (String s)
name|String
name|echo
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|s
return|;
block|}
DECL|method|testCreate ()
annotation|@
name|Test
specifier|public
name|void
name|testCreate
parameter_list|()
block|{
name|WebApp
name|app
init|=
name|WebApps
operator|.
name|$for
argument_list|(
name|this
argument_list|)
operator|.
name|start
argument_list|()
decl_stmt|;
name|app
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|testCreateWithPort ()
annotation|@
name|Test
specifier|public
name|void
name|testCreateWithPort
parameter_list|()
block|{
comment|// see if the ephemeral port is updated
name|WebApp
name|app
init|=
name|WebApps
operator|.
name|$for
argument_list|(
name|this
argument_list|)
operator|.
name|at
argument_list|(
literal|0
argument_list|)
operator|.
name|start
argument_list|()
decl_stmt|;
name|int
name|port
init|=
name|app
operator|.
name|getListenerAddress
argument_list|()
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|port
operator|>
literal|0
argument_list|)
expr_stmt|;
name|app
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// try to reuse the port
name|app
operator|=
name|WebApps
operator|.
name|$for
argument_list|(
name|this
argument_list|)
operator|.
name|at
argument_list|(
name|port
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|port
argument_list|,
name|app
operator|.
name|getListenerAddress
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|app
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|WebAppException
operator|.
name|class
argument_list|)
DECL|method|testCreateWithBindAddressNonZeroPort ()
specifier|public
name|void
name|testCreateWithBindAddressNonZeroPort
parameter_list|()
block|{
name|WebApp
name|app
init|=
name|WebApps
operator|.
name|$for
argument_list|(
name|this
argument_list|)
operator|.
name|at
argument_list|(
literal|"0.0.0.0:50000"
argument_list|)
operator|.
name|start
argument_list|()
decl_stmt|;
name|int
name|port
init|=
name|app
operator|.
name|getListenerAddress
argument_list|()
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|50000
argument_list|,
name|port
argument_list|)
expr_stmt|;
comment|// start another WebApp with same NonZero port
name|WebApp
name|app2
init|=
name|WebApps
operator|.
name|$for
argument_list|(
name|this
argument_list|)
operator|.
name|at
argument_list|(
literal|"0.0.0.0:50000"
argument_list|)
operator|.
name|start
argument_list|()
decl_stmt|;
comment|// An exception occurs (findPort disabled)
name|app
operator|.
name|stop
argument_list|()
expr_stmt|;
name|app2
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|WebAppException
operator|.
name|class
argument_list|)
DECL|method|testCreateWithNonZeroPort ()
specifier|public
name|void
name|testCreateWithNonZeroPort
parameter_list|()
block|{
name|WebApp
name|app
init|=
name|WebApps
operator|.
name|$for
argument_list|(
name|this
argument_list|)
operator|.
name|at
argument_list|(
literal|50000
argument_list|)
operator|.
name|start
argument_list|()
decl_stmt|;
name|int
name|port
init|=
name|app
operator|.
name|getListenerAddress
argument_list|()
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|50000
argument_list|,
name|port
argument_list|)
expr_stmt|;
comment|// start another WebApp with same NonZero port
name|WebApp
name|app2
init|=
name|WebApps
operator|.
name|$for
argument_list|(
name|this
argument_list|)
operator|.
name|at
argument_list|(
literal|50000
argument_list|)
operator|.
name|start
argument_list|()
decl_stmt|;
comment|// An exception occurs (findPort disabled)
name|app
operator|.
name|stop
argument_list|()
expr_stmt|;
name|app2
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|testServePaths ()
annotation|@
name|Test
specifier|public
name|void
name|testServePaths
parameter_list|()
block|{
name|WebApp
name|app
init|=
name|WebApps
operator|.
name|$for
argument_list|(
literal|"test"
argument_list|,
name|this
argument_list|)
operator|.
name|start
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"/test"
argument_list|,
name|app
operator|.
name|getRedirectPath
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|expectedPaths
init|=
block|{
literal|"/test"
block|,
literal|"/test/*"
block|}
decl_stmt|;
name|String
index|[]
name|pathSpecs
init|=
name|app
operator|.
name|getServePathSpecs
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|pathSpecs
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expectedPaths
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|ArrayUtils
operator|.
name|contains
argument_list|(
name|pathSpecs
argument_list|,
name|expectedPaths
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|app
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|testServePathsNoName ()
annotation|@
name|Test
specifier|public
name|void
name|testServePathsNoName
parameter_list|()
block|{
name|WebApp
name|app
init|=
name|WebApps
operator|.
name|$for
argument_list|(
literal|""
argument_list|,
name|this
argument_list|)
operator|.
name|start
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"/"
argument_list|,
name|app
operator|.
name|getRedirectPath
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|expectedPaths
init|=
block|{
literal|"/*"
block|}
decl_stmt|;
name|String
index|[]
name|pathSpecs
init|=
name|app
operator|.
name|getServePathSpecs
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|pathSpecs
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expectedPaths
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|ArrayUtils
operator|.
name|contains
argument_list|(
name|pathSpecs
argument_list|,
name|expectedPaths
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|app
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|testDefaultRoutes ()
annotation|@
name|Test
specifier|public
name|void
name|testDefaultRoutes
parameter_list|()
throws|throws
name|Exception
block|{
name|WebApp
name|app
init|=
name|WebApps
operator|.
name|$for
argument_list|(
literal|"test"
argument_list|,
name|this
argument_list|)
operator|.
name|start
argument_list|()
decl_stmt|;
name|String
name|baseUrl
init|=
name|baseUrl
argument_list|(
name|app
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|getContent
argument_list|(
name|baseUrl
operator|+
literal|"test/foo"
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|getContent
argument_list|(
name|baseUrl
operator|+
literal|"test/foo/index"
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|getContent
argument_list|(
name|baseUrl
operator|+
literal|"test/foo/bar"
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default"
argument_list|,
name|getContent
argument_list|(
name|baseUrl
operator|+
literal|"test"
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default"
argument_list|,
name|getContent
argument_list|(
name|baseUrl
operator|+
literal|"test/"
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default"
argument_list|,
name|getContent
argument_list|(
name|baseUrl
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|app
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testCustomRoutes ()
annotation|@
name|Test
specifier|public
name|void
name|testCustomRoutes
parameter_list|()
throws|throws
name|Exception
block|{
name|WebApp
name|app
init|=
name|WebApps
operator|.
name|$for
argument_list|(
literal|"test"
argument_list|,
name|TestWebApp
operator|.
name|class
argument_list|,
name|this
argument_list|,
literal|"ws"
argument_list|)
operator|.
name|start
argument_list|(
operator|new
name|WebApp
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|bind
argument_list|(
name|MyTestJAXBContextResolver
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|MyTestWebService
operator|.
name|class
argument_list|)
expr_stmt|;
name|route
argument_list|(
literal|"/:foo"
argument_list|,
name|FooController
operator|.
name|class
argument_list|)
expr_stmt|;
name|route
argument_list|(
literal|"/bar/foo"
argument_list|,
name|FooController
operator|.
name|class
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|route
argument_list|(
literal|"/foo/:foo"
argument_list|,
name|DefaultController
operator|.
name|class
argument_list|)
expr_stmt|;
name|route
argument_list|(
literal|"/foo/bar/:foo"
argument_list|,
name|DefaultController
operator|.
name|class
argument_list|,
literal|"index"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|String
name|baseUrl
init|=
name|baseUrl
argument_list|(
name|app
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|getContent
argument_list|(
name|baseUrl
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|getContent
argument_list|(
name|baseUrl
operator|+
literal|"test"
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo1"
argument_list|,
name|getContent
argument_list|(
name|baseUrl
operator|+
literal|"test/1"
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|getContent
argument_list|(
name|baseUrl
operator|+
literal|"test/bar/foo"
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default"
argument_list|,
name|getContent
argument_list|(
name|baseUrl
operator|+
literal|"test/foo/bar"
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default1"
argument_list|,
name|getContent
argument_list|(
name|baseUrl
operator|+
literal|"test/foo/1"
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default2"
argument_list|,
name|getContent
argument_list|(
name|baseUrl
operator|+
literal|"test/foo/bar/2"
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|404
argument_list|,
name|getResponseCode
argument_list|(
name|baseUrl
operator|+
literal|"test/goo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|getResponseCode
argument_list|(
name|baseUrl
operator|+
literal|"ws/v1/test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getContent
argument_list|(
name|baseUrl
operator|+
literal|"ws/v1/test"
argument_list|)
operator|.
name|contains
argument_list|(
literal|"myInfo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|app
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|// This is to test the GuiceFilter should only be applied to webAppContext,
comment|// not to staticContext  and logContext;
DECL|method|testYARNWebAppContext ()
annotation|@
name|Test
specifier|public
name|void
name|testYARNWebAppContext
parameter_list|()
throws|throws
name|Exception
block|{
comment|// setting up the log context
name|System
operator|.
name|setProperty
argument_list|(
literal|"hadoop.log.dir"
argument_list|,
literal|"/Not/Existing/dir"
argument_list|)
expr_stmt|;
name|WebApp
name|app
init|=
name|WebApps
operator|.
name|$for
argument_list|(
literal|"test"
argument_list|,
name|this
argument_list|)
operator|.
name|start
argument_list|(
operator|new
name|WebApp
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|route
argument_list|(
literal|"/"
argument_list|,
name|FooController
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|String
name|baseUrl
init|=
name|baseUrl
argument_list|(
name|app
argument_list|)
decl_stmt|;
try|try
block|{
comment|// should not redirect to foo
name|assertFalse
argument_list|(
literal|"foo"
operator|.
name|equals
argument_list|(
name|getContent
argument_list|(
name|baseUrl
operator|+
literal|"static"
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Not able to access a non-existing dir, should not redirect to foo.
name|assertEquals
argument_list|(
literal|404
argument_list|,
name|getResponseCode
argument_list|(
name|baseUrl
operator|+
literal|"logs"
argument_list|)
argument_list|)
expr_stmt|;
comment|// should be able to redirect to foo.
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|getContent
argument_list|(
name|baseUrl
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|app
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|baseUrl (WebApp app)
specifier|static
name|String
name|baseUrl
parameter_list|(
name|WebApp
name|app
parameter_list|)
block|{
return|return
literal|"http://localhost:"
operator|+
name|app
operator|.
name|port
argument_list|()
operator|+
literal|"/"
return|;
block|}
DECL|method|getContent (String url)
specifier|static
name|String
name|getContent
parameter_list|(
name|String
name|url
parameter_list|)
block|{
try|try
block|{
name|StringBuilder
name|out
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|InputStream
name|in
init|=
operator|new
name|URL
argument_list|(
name|url
argument_list|)
operator|.
name|openConnection
argument_list|()
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|64
operator|*
literal|1024
index|]
decl_stmt|;
name|int
name|len
init|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
operator|new
name|String
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
argument_list|)
expr_stmt|;
name|len
operator|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
return|return
name|out
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getResponseCode (String url)
specifier|static
name|int
name|getResponseCode
parameter_list|(
name|String
name|url
parameter_list|)
block|{
try|try
block|{
name|HttpURLConnection
name|c
init|=
operator|(
name|HttpURLConnection
operator|)
operator|new
name|URL
argument_list|(
name|url
argument_list|)
operator|.
name|openConnection
argument_list|()
decl_stmt|;
return|return
name|c
operator|.
name|getResponseCode
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
comment|// For manual controller/view testing.
name|WebApps
operator|.
name|$for
argument_list|(
literal|"test"
argument_list|,
operator|new
name|TestWebApp
argument_list|()
argument_list|)
operator|.
name|at
argument_list|(
literal|8888
argument_list|)
operator|.
name|inDevMode
argument_list|()
operator|.
name|start
argument_list|()
operator|.
name|joinThread
argument_list|()
expr_stmt|;
comment|//        start(new WebApp() {
comment|//          @Override public void setup() {
comment|//            route("/:foo", FooController.class);
comment|//            route("/foo/:foo", FooController.class);
comment|//            route("/bar", FooController.class);
comment|//          }
comment|//        }).join();
block|}
block|}
end_class

end_unit

