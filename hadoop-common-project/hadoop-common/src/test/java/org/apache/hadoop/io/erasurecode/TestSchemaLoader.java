begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.erasurecode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|erasurecode
package|;
end_package

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
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestSchemaLoader
specifier|public
class|class
name|TestSchemaLoader
block|{
DECL|field|TEST_DIR
specifier|final
specifier|static
name|String
name|TEST_DIR
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
DECL|field|SCHEMA_FILE
specifier|final
specifier|static
name|String
name|SCHEMA_FILE
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"test-ecschema"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|testLoadSchema ()
specifier|public
name|void
name|testLoadSchema
parameter_list|()
throws|throws
name|Exception
block|{
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|SCHEMA_FILE
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<?xml version=\"1.0\"?>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<schemas>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<schema name=\"RSk6m3\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<k>6</k>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<m>3</m>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<codec>RS</codec>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</schema>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<schema name=\"RSk10m4\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<k>10</k>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<m>4</m>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<codec>RS</codec>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</schema>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</schemas>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|SchemaLoader
name|schemaLoader
init|=
operator|new
name|SchemaLoader
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ECSchema
argument_list|>
name|schemas
init|=
name|schemaLoader
operator|.
name|loadSchema
argument_list|(
name|SCHEMA_FILE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|schemas
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ECSchema
name|schema1
init|=
name|schemas
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"RSk6m3"
argument_list|,
name|schema1
operator|.
name|getSchemaName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|schema1
operator|.
name|getOptions
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|schema1
operator|.
name|getNumDataUnits
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|schema1
operator|.
name|getNumParityUnits
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"RS"
argument_list|,
name|schema1
operator|.
name|getCodecName
argument_list|()
argument_list|)
expr_stmt|;
name|ECSchema
name|schema2
init|=
name|schemas
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"RSk10m4"
argument_list|,
name|schema2
operator|.
name|getSchemaName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|schema2
operator|.
name|getOptions
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|schema2
operator|.
name|getNumDataUnits
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|schema2
operator|.
name|getNumParityUnits
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"RS"
argument_list|,
name|schema2
operator|.
name|getCodecName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

