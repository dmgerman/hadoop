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
name|DatabaseMetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Driver
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverPropertyInfo
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
name|SQLFeatureNotSupportedException
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
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|any
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

begin_comment
comment|/**  * class emulates a connection to database  *   */
end_comment

begin_class
DECL|class|DriverForTest
specifier|public
class|class
name|DriverForTest
implements|implements
name|Driver
block|{
DECL|method|getConnection ()
specifier|public
specifier|static
name|Connection
name|getConnection
parameter_list|()
block|{
name|Connection
name|connection
init|=
name|mock
argument_list|(
name|FakeConnection
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
name|Statement
name|statement
init|=
name|mock
argument_list|(
name|Statement
operator|.
name|class
argument_list|)
decl_stmt|;
name|ResultSet
name|results
init|=
name|mock
argument_list|(
name|ResultSet
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|results
operator|.
name|getLong
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|15L
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|statement
operator|.
name|executeQuery
argument_list|(
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|results
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|connection
operator|.
name|createStatement
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|statement
argument_list|)
expr_stmt|;
name|DatabaseMetaData
name|metadata
init|=
name|mock
argument_list|(
name|DatabaseMetaData
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|metadata
operator|.
name|getDatabaseProductName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"Test"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|connection
operator|.
name|getMetaData
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
name|PreparedStatement
name|reparedStatement0
init|=
name|mock
argument_list|(
name|PreparedStatement
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|connection
operator|.
name|prepareStatement
argument_list|(
name|anyString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|reparedStatement0
argument_list|)
expr_stmt|;
name|PreparedStatement
name|preparedStatement
init|=
name|mock
argument_list|(
name|PreparedStatement
operator|.
name|class
argument_list|)
decl_stmt|;
name|ResultSet
name|resultSet
init|=
name|mock
argument_list|(
name|ResultSet
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|preparedStatement
operator|.
name|executeQuery
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|resultSet
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|connection
operator|.
name|prepareStatement
argument_list|(
name|anyString
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|preparedStatement
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
empty_stmt|;
block|}
return|return
name|connection
return|;
block|}
annotation|@
name|Override
DECL|method|acceptsURL (String arg0)
specifier|public
name|boolean
name|acceptsURL
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
literal|"testUrl"
operator|.
name|equals
argument_list|(
name|arg0
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|connect (String arg0, Properties arg1)
specifier|public
name|Connection
name|connect
parameter_list|(
name|String
name|arg0
parameter_list|,
name|Properties
name|arg1
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|getConnection
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getMajorVersion ()
specifier|public
name|int
name|getMajorVersion
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getMinorVersion ()
specifier|public
name|int
name|getMinorVersion
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getPropertyInfo (String arg0, Properties arg1)
specifier|public
name|DriverPropertyInfo
index|[]
name|getPropertyInfo
parameter_list|(
name|String
name|arg0
parameter_list|,
name|Properties
name|arg1
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|jdbcCompliant ()
specifier|public
name|boolean
name|jdbcCompliant
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|getParentLogger ()
specifier|public
name|Logger
name|getParentLogger
parameter_list|()
throws|throws
name|SQLFeatureNotSupportedException
block|{
throw|throw
operator|new
name|SQLFeatureNotSupportedException
argument_list|()
throw|;
block|}
DECL|interface|FakeConnection
specifier|private
interface|interface
name|FakeConnection
extends|extends
name|Connection
block|{
DECL|method|setSessionTimeZone (String arg)
specifier|public
name|void
name|setSessionTimeZone
parameter_list|(
name|String
name|arg
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

