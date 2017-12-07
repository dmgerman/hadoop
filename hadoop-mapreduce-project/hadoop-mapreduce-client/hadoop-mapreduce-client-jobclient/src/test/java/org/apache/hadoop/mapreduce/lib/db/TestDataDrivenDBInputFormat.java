begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.db
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|db
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
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
name|fs
operator|.
name|FileSystem
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
name|fs
operator|.
name|Path
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
name|io
operator|.
name|NullWritable
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
name|io
operator|.
name|WritableComparable
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
name|mapred
operator|.
name|HadoopTestCase
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
name|mapreduce
operator|.
name|Job
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
name|mapreduce
operator|.
name|Mapper
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
name|mapreduce
operator|.
name|Reducer
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
name|mapreduce
operator|.
name|TaskCounter
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
name|mapreduce
operator|.
name|lib
operator|.
name|output
operator|.
name|FileOutputFormat
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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hsqldb
operator|.
name|server
operator|.
name|Server
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|PreparedStatement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
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
name|assertTrue
import|;
end_import

begin_comment
comment|//import org.apache.hadoop.examples.DBCountPageView;
end_comment

begin_comment
comment|/**  * Test aspects of DataDrivenDBInputFormat  */
end_comment

begin_class
DECL|class|TestDataDrivenDBInputFormat
specifier|public
class|class
name|TestDataDrivenDBInputFormat
extends|extends
name|HadoopTestCase
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestDataDrivenDBInputFormat
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DB_NAME
specifier|private
specifier|static
specifier|final
name|String
name|DB_NAME
init|=
literal|"dddbif"
decl_stmt|;
DECL|field|DB_URL
specifier|private
specifier|static
specifier|final
name|String
name|DB_URL
init|=
literal|"jdbc:hsqldb:hsql://localhost/"
operator|+
name|DB_NAME
decl_stmt|;
DECL|field|DRIVER_CLASS
specifier|private
specifier|static
specifier|final
name|String
name|DRIVER_CLASS
init|=
literal|"org.hsqldb.jdbc.JDBCDriver"
decl_stmt|;
DECL|field|server
specifier|private
name|Server
name|server
decl_stmt|;
DECL|field|connection
specifier|private
name|Connection
name|connection
decl_stmt|;
DECL|field|OUT_DIR
specifier|private
specifier|static
specifier|final
name|String
name|OUT_DIR
decl_stmt|;
DECL|method|TestDataDrivenDBInputFormat ()
specifier|public
name|TestDataDrivenDBInputFormat
parameter_list|()
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|LOCAL_MR
argument_list|,
name|LOCAL_FS
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
static|static
block|{
name|OUT_DIR
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
operator|+
literal|"/dddbifout"
expr_stmt|;
block|}
DECL|method|startHsqldbServer ()
specifier|private
name|void
name|startHsqldbServer
parameter_list|()
block|{
if|if
condition|(
literal|null
operator|==
name|server
condition|)
block|{
name|server
operator|=
operator|new
name|Server
argument_list|()
expr_stmt|;
name|server
operator|.
name|setDatabasePath
argument_list|(
literal|0
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
operator|+
literal|"/"
operator|+
name|DB_NAME
argument_list|)
expr_stmt|;
name|server
operator|.
name|setDatabaseName
argument_list|(
literal|0
argument_list|,
name|DB_NAME
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createConnection (String driverClassName, String url)
specifier|private
name|void
name|createConnection
parameter_list|(
name|String
name|driverClassName
parameter_list|,
name|String
name|url
parameter_list|)
throws|throws
name|Exception
block|{
name|Class
operator|.
name|forName
argument_list|(
name|driverClassName
argument_list|)
expr_stmt|;
name|connection
operator|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setAutoCommit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|shutdown ()
specifier|private
name|void
name|shutdown
parameter_list|()
block|{
try|try
block|{
name|connection
operator|.
name|commit
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception occurred while closing connection :"
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|ex
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception occurred while shutting down HSQLDB :"
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|ex
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|server
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|initialize (String driverClassName, String url)
specifier|private
name|void
name|initialize
parameter_list|(
name|String
name|driverClassName
parameter_list|,
name|String
name|url
parameter_list|)
throws|throws
name|Exception
block|{
name|startHsqldbServer
argument_list|()
expr_stmt|;
name|createConnection
argument_list|(
name|driverClassName
argument_list|,
name|url
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|initialize
argument_list|(
name|DRIVER_CLASS
argument_list|,
name|DB_URL
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|class|DateCol
specifier|public
specifier|static
class|class
name|DateCol
implements|implements
name|DBWritable
implements|,
name|WritableComparable
block|{
DECL|field|d
name|Date
name|d
decl_stmt|;
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|d
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|readFields (ResultSet rs)
specifier|public
name|void
name|readFields
parameter_list|(
name|ResultSet
name|rs
parameter_list|)
throws|throws
name|SQLException
block|{
name|d
operator|=
name|rs
operator|.
name|getDate
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|write (PreparedStatement ps)
specifier|public
name|void
name|write
parameter_list|(
name|PreparedStatement
name|ps
parameter_list|)
block|{
comment|// not needed.
block|}
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|v
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|d
operator|=
operator|new
name|Date
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|d
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|d
operator|.
name|getTime
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (Object o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|DateCol
condition|)
block|{
name|Long
name|v
init|=
name|Long
operator|.
name|valueOf
argument_list|(
name|d
operator|.
name|getTime
argument_list|()
argument_list|)
decl_stmt|;
name|Long
name|other
init|=
name|Long
operator|.
name|valueOf
argument_list|(
operator|(
operator|(
name|DateCol
operator|)
name|o
operator|)
operator|.
name|d
operator|.
name|getTime
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|v
operator|.
name|compareTo
argument_list|(
name|other
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
block|}
DECL|class|ValMapper
specifier|public
specifier|static
class|class
name|ValMapper
extends|extends
name|Mapper
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|,
name|Object
argument_list|,
name|NullWritable
argument_list|>
block|{
DECL|method|map (Object k, Object v, Context c)
specifier|public
name|void
name|map
parameter_list|(
name|Object
name|k
parameter_list|,
name|Object
name|v
parameter_list|,
name|Context
name|c
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|c
operator|.
name|write
argument_list|(
name|v
argument_list|,
name|NullWritable
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDateSplits ()
specifier|public
name|void
name|testDateSplits
parameter_list|()
throws|throws
name|Exception
block|{
name|Statement
name|s
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
specifier|final
name|String
name|DATE_TABLE
init|=
literal|"datetable"
decl_stmt|;
specifier|final
name|String
name|COL
init|=
literal|"foo"
decl_stmt|;
try|try
block|{
comment|// delete the table if it already exists.
name|s
operator|.
name|executeUpdate
argument_list|(
literal|"DROP TABLE "
operator|+
name|DATE_TABLE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{     }
comment|// Create the table.
name|s
operator|.
name|executeUpdate
argument_list|(
literal|"CREATE TABLE "
operator|+
name|DATE_TABLE
operator|+
literal|"("
operator|+
name|COL
operator|+
literal|" DATE)"
argument_list|)
expr_stmt|;
name|s
operator|.
name|executeUpdate
argument_list|(
literal|"INSERT INTO "
operator|+
name|DATE_TABLE
operator|+
literal|" VALUES('2010-04-01')"
argument_list|)
expr_stmt|;
name|s
operator|.
name|executeUpdate
argument_list|(
literal|"INSERT INTO "
operator|+
name|DATE_TABLE
operator|+
literal|" VALUES('2010-04-02')"
argument_list|)
expr_stmt|;
name|s
operator|.
name|executeUpdate
argument_list|(
literal|"INSERT INTO "
operator|+
name|DATE_TABLE
operator|+
literal|" VALUES('2010-05-01')"
argument_list|)
expr_stmt|;
name|s
operator|.
name|executeUpdate
argument_list|(
literal|"INSERT INTO "
operator|+
name|DATE_TABLE
operator|+
literal|" VALUES('2011-04-01')"
argument_list|)
expr_stmt|;
comment|// commit this tx.
name|connection
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"fs.defaultFS"
argument_list|,
literal|"file:///"
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|OUT_DIR
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// now do a dd import
name|Job
name|job
init|=
name|Job
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|ValMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setReducerClass
argument_list|(
name|Reducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapOutputKeyClass
argument_list|(
name|DateCol
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapOutputValueClass
argument_list|(
name|NullWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputKeyClass
argument_list|(
name|DateCol
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputValueClass
argument_list|(
name|NullWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setNumReduceTasks
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setInt
argument_list|(
literal|"mapreduce.map.tasks"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|job
argument_list|,
operator|new
name|Path
argument_list|(
name|OUT_DIR
argument_list|)
argument_list|)
expr_stmt|;
name|DBConfiguration
operator|.
name|configureDB
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|DRIVER_CLASS
argument_list|,
name|DB_URL
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|DataDrivenDBInputFormat
operator|.
name|setInput
argument_list|(
name|job
argument_list|,
name|DateCol
operator|.
name|class
argument_list|,
name|DATE_TABLE
argument_list|,
literal|null
argument_list|,
name|COL
argument_list|,
name|COL
argument_list|)
expr_stmt|;
name|boolean
name|ret
init|=
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"job failed"
argument_list|,
name|ret
argument_list|)
expr_stmt|;
comment|// Check to see that we imported as much as we thought we did.
name|assertEquals
argument_list|(
literal|"Did not get all the records"
argument_list|,
literal|4
argument_list|,
name|job
operator|.
name|getCounters
argument_list|()
operator|.
name|findCounter
argument_list|(
name|TaskCounter
operator|.
name|REDUCE_OUTPUT_RECORDS
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

