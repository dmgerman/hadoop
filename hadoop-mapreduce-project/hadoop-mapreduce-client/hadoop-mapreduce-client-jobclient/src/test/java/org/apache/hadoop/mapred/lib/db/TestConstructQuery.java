begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.lib.db
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|mapred
operator|.
name|JobConf
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

begin_class
DECL|class|TestConstructQuery
specifier|public
class|class
name|TestConstructQuery
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
literal|"INSERT INTO hadoop_output (id,name,value) VALUES (?,?,?);"
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
DECL|method|testSetOutput ()
specifier|public
name|void
name|testSetOutput
parameter_list|()
throws|throws
name|IOException
block|{
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|()
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
operator|new
name|JobConf
argument_list|()
expr_stmt|;
name|dbConf
operator|=
operator|new
name|DBConfiguration
argument_list|(
name|job
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

