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
name|Types
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|InputSplit
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
name|JobContext
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
name|MRJobConfig
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
name|db
operator|.
name|DBInputFormat
operator|.
name|DBInputSplit
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
name|db
operator|.
name|DBInputFormat
operator|.
name|NullDBWritable
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
name|db
operator|.
name|DataDrivenDBInputFormat
operator|.
name|DataDrivenDBInputSplit
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
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestDbClasses
specifier|public
class|class
name|TestDbClasses
block|{
comment|/**    * test splitters from DataDrivenDBInputFormat. For different data types may    * be different splitter    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testDataDrivenDBInputFormatSplitter ()
specifier|public
name|void
name|testDataDrivenDBInputFormatSplitter
parameter_list|()
block|{
name|DataDrivenDBInputFormat
argument_list|<
name|NullDBWritable
argument_list|>
name|format
init|=
operator|new
name|DataDrivenDBInputFormat
argument_list|<
name|NullDBWritable
argument_list|>
argument_list|()
decl_stmt|;
name|testCommonSplitterTypes
argument_list|(
name|format
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DateSplitter
operator|.
name|class
argument_list|,
name|format
operator|.
name|getSplitter
argument_list|(
name|Types
operator|.
name|TIMESTAMP
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DateSplitter
operator|.
name|class
argument_list|,
name|format
operator|.
name|getSplitter
argument_list|(
name|Types
operator|.
name|DATE
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DateSplitter
operator|.
name|class
argument_list|,
name|format
operator|.
name|getSplitter
argument_list|(
name|Types
operator|.
name|TIME
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testDataDrivenDBInputFormat ()
specifier|public
name|void
name|testDataDrivenDBInputFormat
parameter_list|()
throws|throws
name|Exception
block|{
name|JobContext
name|jobContext
init|=
name|mock
argument_list|(
name|JobContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|configuration
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|NUM_MAPS
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|jobContext
operator|.
name|getConfiguration
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|DataDrivenDBInputFormat
argument_list|<
name|NullDBWritable
argument_list|>
name|format
init|=
operator|new
name|DataDrivenDBInputFormat
argument_list|<
name|NullDBWritable
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|InputSplit
argument_list|>
name|splits
init|=
name|format
operator|.
name|getSplits
argument_list|(
name|jobContext
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|splits
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|DataDrivenDBInputSplit
name|split
init|=
operator|(
name|DataDrivenDBInputSplit
operator|)
name|splits
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1=1"
argument_list|,
name|split
operator|.
name|getLowerClause
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1=1"
argument_list|,
name|split
operator|.
name|getUpperClause
argument_list|()
argument_list|)
expr_stmt|;
comment|// 2
name|configuration
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|NUM_MAPS
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|DataDrivenDBInputFormat
operator|.
name|setBoundingQuery
argument_list|(
name|configuration
argument_list|,
literal|"query"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"query"
argument_list|,
name|configuration
operator|.
name|get
argument_list|(
name|DBConfiguration
operator|.
name|INPUT_BOUNDING_QUERY
argument_list|)
argument_list|)
expr_stmt|;
name|Job
name|job
init|=
name|mock
argument_list|(
name|Job
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|DataDrivenDBInputFormat
operator|.
name|setInput
argument_list|(
name|job
argument_list|,
name|NullDBWritable
operator|.
name|class
argument_list|,
literal|"query"
argument_list|,
literal|"Bounding Query"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Bounding Query"
argument_list|,
name|configuration
operator|.
name|get
argument_list|(
name|DBConfiguration
operator|.
name|INPUT_BOUNDING_QUERY
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testOracleDataDrivenDBInputFormat ()
specifier|public
name|void
name|testOracleDataDrivenDBInputFormat
parameter_list|()
throws|throws
name|Exception
block|{
name|OracleDataDrivenDBInputFormat
argument_list|<
name|NullDBWritable
argument_list|>
name|format
init|=
operator|new
name|OracleDataDrivenDBInputFormatForTest
argument_list|()
decl_stmt|;
name|testCommonSplitterTypes
argument_list|(
name|format
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OracleDateSplitter
operator|.
name|class
argument_list|,
name|format
operator|.
name|getSplitter
argument_list|(
name|Types
operator|.
name|TIMESTAMP
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OracleDateSplitter
operator|.
name|class
argument_list|,
name|format
operator|.
name|getSplitter
argument_list|(
name|Types
operator|.
name|DATE
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OracleDateSplitter
operator|.
name|class
argument_list|,
name|format
operator|.
name|getSplitter
argument_list|(
name|Types
operator|.
name|TIME
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * test generate sql script for OracleDBRecordReader.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
DECL|method|testOracleDBRecordReader ()
specifier|public
name|void
name|testOracleDBRecordReader
parameter_list|()
throws|throws
name|Exception
block|{
name|DBInputSplit
name|splitter
init|=
operator|new
name|DBInputSplit
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|Connection
name|connect
init|=
name|DriverForTest
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|DBConfiguration
name|dbConfiguration
init|=
operator|new
name|DBConfiguration
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
name|dbConfiguration
operator|.
name|setInputOrderBy
argument_list|(
literal|"Order"
argument_list|)
expr_stmt|;
name|String
index|[]
name|fields
init|=
block|{
literal|"f1"
block|,
literal|"f2"
block|}
decl_stmt|;
name|OracleDBRecordReader
argument_list|<
name|NullDBWritable
argument_list|>
name|recorder
init|=
operator|new
name|OracleDBRecordReader
argument_list|<
name|NullDBWritable
argument_list|>
argument_list|(
name|splitter
argument_list|,
name|NullDBWritable
operator|.
name|class
argument_list|,
name|configuration
argument_list|,
name|connect
argument_list|,
name|dbConfiguration
argument_list|,
literal|"condition"
argument_list|,
name|fields
argument_list|,
literal|"table"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"SELECT * FROM (SELECT a.*,ROWNUM dbif_rno FROM ( SELECT f1, f2 FROM table WHERE condition ORDER BY Order ) a WHERE rownum<= 10 ) WHERE dbif_rno> 1"
argument_list|,
name|recorder
operator|.
name|getSelectQuery
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testCommonSplitterTypes ( DataDrivenDBInputFormat<NullDBWritable> format)
specifier|private
name|void
name|testCommonSplitterTypes
parameter_list|(
name|DataDrivenDBInputFormat
argument_list|<
name|NullDBWritable
argument_list|>
name|format
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|BigDecimalSplitter
operator|.
name|class
argument_list|,
name|format
operator|.
name|getSplitter
argument_list|(
name|Types
operator|.
name|DECIMAL
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BigDecimalSplitter
operator|.
name|class
argument_list|,
name|format
operator|.
name|getSplitter
argument_list|(
name|Types
operator|.
name|NUMERIC
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BooleanSplitter
operator|.
name|class
argument_list|,
name|format
operator|.
name|getSplitter
argument_list|(
name|Types
operator|.
name|BOOLEAN
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BooleanSplitter
operator|.
name|class
argument_list|,
name|format
operator|.
name|getSplitter
argument_list|(
name|Types
operator|.
name|BIT
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IntegerSplitter
operator|.
name|class
argument_list|,
name|format
operator|.
name|getSplitter
argument_list|(
name|Types
operator|.
name|BIGINT
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IntegerSplitter
operator|.
name|class
argument_list|,
name|format
operator|.
name|getSplitter
argument_list|(
name|Types
operator|.
name|TINYINT
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IntegerSplitter
operator|.
name|class
argument_list|,
name|format
operator|.
name|getSplitter
argument_list|(
name|Types
operator|.
name|SMALLINT
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IntegerSplitter
operator|.
name|class
argument_list|,
name|format
operator|.
name|getSplitter
argument_list|(
name|Types
operator|.
name|INTEGER
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FloatSplitter
operator|.
name|class
argument_list|,
name|format
operator|.
name|getSplitter
argument_list|(
name|Types
operator|.
name|DOUBLE
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FloatSplitter
operator|.
name|class
argument_list|,
name|format
operator|.
name|getSplitter
argument_list|(
name|Types
operator|.
name|REAL
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FloatSplitter
operator|.
name|class
argument_list|,
name|format
operator|.
name|getSplitter
argument_list|(
name|Types
operator|.
name|FLOAT
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TextSplitter
operator|.
name|class
argument_list|,
name|format
operator|.
name|getSplitter
argument_list|(
name|Types
operator|.
name|LONGVARCHAR
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TextSplitter
operator|.
name|class
argument_list|,
name|format
operator|.
name|getSplitter
argument_list|(
name|Types
operator|.
name|CHAR
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TextSplitter
operator|.
name|class
argument_list|,
name|format
operator|.
name|getSplitter
argument_list|(
name|Types
operator|.
name|VARCHAR
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
comment|// if unknown data type splitter is null
name|assertNull
argument_list|(
name|format
operator|.
name|getSplitter
argument_list|(
name|Types
operator|.
name|BINARY
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|OracleDataDrivenDBInputFormatForTest
specifier|private
class|class
name|OracleDataDrivenDBInputFormatForTest
extends|extends
name|OracleDataDrivenDBInputFormat
argument_list|<
name|NullDBWritable
argument_list|>
block|{
annotation|@
name|Override
DECL|method|getDBConf ()
specifier|public
name|DBConfiguration
name|getDBConf
parameter_list|()
block|{
name|String
index|[]
name|names
init|=
block|{
literal|"field1"
block|,
literal|"field2"
block|}
decl_stmt|;
name|DBConfiguration
name|result
init|=
name|mock
argument_list|(
name|DBConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|result
operator|.
name|getInputConditions
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"conditions"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|result
operator|.
name|getInputFieldNames
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|names
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|result
operator|.
name|getInputTableName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"table"
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|getConnection ()
specifier|public
name|Connection
name|getConnection
parameter_list|()
block|{
return|return
name|DriverForTest
operator|.
name|getConnection
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

