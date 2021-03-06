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
name|lang
operator|.
name|reflect
operator|.
name|Field
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
name|lang3
operator|.
name|StringUtils
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
name|mapreduce
operator|.
name|Job
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
name|assertNull
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
name|fail
import|;
end_import

begin_class
DECL|class|TestDBOutputFormat
specifier|public
class|class
name|TestDBOutputFormat
block|{
DECL|field|fieldNames
specifier|private
name|String
index|[]
name|fieldNames
init|=
operator|new
name|String
index|[]
block|{
literal|"id"
block|,
literal|"name"
block|,
literal|"value"
block|}
decl_stmt|;
DECL|field|nullFieldNames
specifier|private
name|String
index|[]
name|nullFieldNames
init|=
operator|new
name|String
index|[]
block|{
literal|null
block|,
literal|null
block|,
literal|null
block|}
decl_stmt|;
DECL|field|expected
specifier|private
name|String
name|expected
init|=
literal|"INSERT INTO hadoop_output "
operator|+
literal|"(id,name,value) VALUES (?,?,?);"
decl_stmt|;
DECL|field|nullExpected
specifier|private
name|String
name|nullExpected
init|=
literal|"INSERT INTO hadoop_output VALUES (?,?,?);"
decl_stmt|;
DECL|field|format
specifier|private
name|DBOutputFormat
argument_list|<
name|DBWritable
argument_list|,
name|NullWritable
argument_list|>
name|format
init|=
operator|new
name|DBOutputFormat
argument_list|<
name|DBWritable
argument_list|,
name|NullWritable
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|testConstructQuery ()
specifier|public
name|void
name|testConstructQuery
parameter_list|()
block|{
name|String
name|actual
init|=
name|format
operator|.
name|constructQuery
argument_list|(
literal|"hadoop_output"
argument_list|,
name|fieldNames
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|actual
operator|=
name|format
operator|.
name|constructQuery
argument_list|(
literal|"hadoop_output"
argument_list|,
name|nullFieldNames
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|nullExpected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDB2ConstructQuery ()
specifier|public
name|void
name|testDB2ConstructQuery
parameter_list|()
block|{
name|String
name|db2expected
init|=
name|StringUtils
operator|.
name|removeEnd
argument_list|(
name|expected
argument_list|,
literal|";"
argument_list|)
decl_stmt|;
name|String
name|db2nullExpected
init|=
name|StringUtils
operator|.
name|removeEnd
argument_list|(
name|nullExpected
argument_list|,
literal|";"
argument_list|)
decl_stmt|;
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|this
operator|.
name|format
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|Field
name|field
init|=
name|clazz
operator|.
name|getDeclaredField
argument_list|(
literal|"dbProductName"
argument_list|)
decl_stmt|;
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|field
operator|.
name|set
argument_list|(
name|format
argument_list|,
literal|"DB2"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
decl||
name|NoSuchFieldException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|actual
init|=
name|format
operator|.
name|constructQuery
argument_list|(
literal|"hadoop_output"
argument_list|,
name|fieldNames
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|db2expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|actual
operator|=
name|format
operator|.
name|constructQuery
argument_list|(
literal|"hadoop_output"
argument_list|,
name|nullFieldNames
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|db2nullExpected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testORACLEConstructQuery ()
specifier|public
name|void
name|testORACLEConstructQuery
parameter_list|()
block|{
name|String
name|oracleExpected
init|=
name|StringUtils
operator|.
name|removeEnd
argument_list|(
name|expected
argument_list|,
literal|";"
argument_list|)
decl_stmt|;
name|String
name|oracleNullExpected
init|=
name|StringUtils
operator|.
name|removeEnd
argument_list|(
name|nullExpected
argument_list|,
literal|";"
argument_list|)
decl_stmt|;
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|this
operator|.
name|format
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|Field
name|field
init|=
name|clazz
operator|.
name|getDeclaredField
argument_list|(
literal|"dbProductName"
argument_list|)
decl_stmt|;
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|field
operator|.
name|set
argument_list|(
name|format
argument_list|,
literal|"ORACLE"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
decl||
name|NoSuchFieldException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|actual
init|=
name|format
operator|.
name|constructQuery
argument_list|(
literal|"hadoop_output"
argument_list|,
name|fieldNames
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|oracleExpected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|actual
operator|=
name|format
operator|.
name|constructQuery
argument_list|(
literal|"hadoop_output"
argument_list|,
name|nullFieldNames
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oracleNullExpected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSetOutput ()
specifier|public
name|void
name|testSetOutput
parameter_list|()
throws|throws
name|IOException
block|{
name|Job
name|job
init|=
name|Job
operator|.
name|getInstance
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|DBOutputFormat
operator|.
name|setOutput
argument_list|(
name|job
argument_list|,
literal|"hadoop_output"
argument_list|,
name|fieldNames
argument_list|)
expr_stmt|;
name|DBConfiguration
name|dbConf
init|=
operator|new
name|DBConfiguration
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|actual
init|=
name|format
operator|.
name|constructQuery
argument_list|(
name|dbConf
operator|.
name|getOutputTableName
argument_list|()
argument_list|,
name|dbConf
operator|.
name|getOutputFieldNames
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|job
operator|=
name|Job
operator|.
name|getInstance
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|dbConf
operator|=
operator|new
name|DBConfiguration
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|DBOutputFormat
operator|.
name|setOutput
argument_list|(
name|job
argument_list|,
literal|"hadoop_output"
argument_list|,
name|nullFieldNames
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|dbConf
operator|.
name|getOutputFieldNames
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|nullFieldNames
operator|.
name|length
argument_list|,
name|dbConf
operator|.
name|getOutputFieldCount
argument_list|()
argument_list|)
expr_stmt|;
name|actual
operator|=
name|format
operator|.
name|constructQuery
argument_list|(
name|dbConf
operator|.
name|getOutputTableName
argument_list|()
argument_list|,
operator|new
name|String
index|[
name|dbConf
operator|.
name|getOutputFieldCount
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|nullExpected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

