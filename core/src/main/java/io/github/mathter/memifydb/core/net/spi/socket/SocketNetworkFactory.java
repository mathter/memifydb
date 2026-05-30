package io.github.mathter.memifydb.core.net.spi.socket;

import io.github.mathter.memifydb.command.CommandSerializationProvider;
import io.github.mathter.memifydb.command.ResultSerializationProvider;
import io.github.mathter.memifydb.core.net.Network;
import io.github.mathter.memifydb.core.net.NetworkFactory;
import io.github.mathter.memifydb.core.net.socket.Const;
import io.github.mathter.memifydb.universe.Universe;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Copyright 2026 Alexander Kashirsky (mathter)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class SocketNetworkFactory extends NetworkFactory {
    @Override
    public Network newInstance(Map<?, ?> properties) {
        return new SocketNetwork(
                buildInetAddress(properties),
                buidPort(properties),
                buidBacklog(properties),
                buidMaxConnectionCount(properties),
                buildCommandSerelizationFactory(properties),
                buildResultSerializationFactory(properties),
                buildUniverses(properties)
        );
    }

    private static InetAddress buildInetAddress(Map<?, ?> properties) {
        final InetAddress result;
        final Object object;

        if ((object = properties.get(Const.PROPERTY_ADDRESS)) != null) {
            if (object instanceof InetAddress inetAddress) {
                result = inetAddress;
            } else if (object instanceof String string) {
                try {
                    result = InetAddress.getByName(string);
                } catch (UnknownHostException e) {
                    throw new RuntimeException(String.format("Can't get host from %s!", string), e);
                }
            } else {
                throw new RuntimeException(String.format("Can't get host from %s!", object));
            }
        } else {
            throw new IllegalArgumentException(
                    String.format("The property '%s' is required", Const.PROPERTY_ADDRESS)
            );
        }

        return result;
    }

    private static int buidPort(Map<?, ?> properties) {
        final int result;
        final Object object;

        if ((object = properties.get(Const.PROPERTY_PORT)) != null) {
            if (object instanceof Number number) {
                result = number.intValue();
            } else if (object instanceof String string) {
                result = Integer.parseInt(string);
            } else {
                throw new IllegalArgumentException(
                        String.format("Invalid value %s for property '%s'", object, Const.PROPERTY_PORT)
                );
            }
        } else {
            throw new IllegalArgumentException(
                    String.format("The property '%s' is required", Const.PROPERTY_PORT)
            );
        }

        return result;
    }

    private static int buidBacklog(Map<?, ?> properties) {
        final int result;
        final Object object;

        if ((object = properties.get(Const.PROPERTY_BACKLOG)) != null) {
            if (object instanceof Number number) {
                result = number.intValue();
            } else if (object instanceof String string) {
                result = Integer.parseInt(string);
            } else {
                throw new IllegalArgumentException(
                        String.format("Invalid value %s for property '%s'", object, Const.PROPERTY_BACKLOG)
                );
            }
        } else {
            throw new IllegalArgumentException(
                    String.format("The property '%s' is required", Const.PROPERTY_BACKLOG)
            );
        }

        return result;
    }

    private static int buidMaxConnectionCount(Map<?, ?> properties) {
        final int result;
        final Object object;

        if ((object = properties.get(Const.PROPERTY_MAXCONNECTIONCOUNT)) != null) {
            if (object instanceof Number number) {
                result = number.intValue();
            } else if (object instanceof String string) {
                result = Integer.parseInt(string);
            } else {
                throw new IllegalArgumentException(
                        String.format("Invalid value %s for property '%s'", object, Const.PROPERTY_MAXCONNECTIONCOUNT)
                );
            }
        } else {
            throw new IllegalArgumentException(
                    String.format("The property '%s' is required", Const.PROPERTY_MAXCONNECTIONCOUNT)
            );
        }

        return result;
    }

    private static Collection<Universe> buildUniverses(Map<?, ?> properties) {
        final Collection<Universe> result;
        final Object object;

        if ((object = properties.get(Const.PROPERTY_UNIVERSES)) != null) {
            if (object instanceof Collection collection) {
                result = new ArrayList<>();

                for (Object element : collection) {
                    if (element instanceof Universe universe) {
                        result.add(universe);
                    }
                }
            } else {
                throw new IllegalArgumentException(String.format("Invalid value %s of the property %s", object, Const.PROPERTY_UNIVERSES));
            }
        } else {
            throw new IllegalArgumentException(String.format("The property '%s' is required", Const.PROPERTY_UNIVERSES));
        }

        return result;
    }

    private CommandSerializationProvider buildCommandSerelizationFactory(Map<?, ?> properties) {
        final CommandSerializationProvider result;
        final Object object;

        if ((object = properties.get(Const.PROPERTY_COMMAND_SERIALIZATION_FACTORY)) != null) {
            if (object instanceof CommandSerializationProvider factory) {
                result = factory;
            } else if (object instanceof String string) {
                result = CommandSerializationProvider.get(string);
            } else {
                throw new IllegalArgumentException(
                        String.format(
                                "The property '%s'=%s is incorrect!",
                                Const.PROPERTY_COMMAND_SERIALIZATION_FACTORY, object
                        )
                );
            }
        } else {
            result = CommandSerializationProvider.get(io.github.mathter.memifydb.command.v1.Const.ID);
        }

        return result;
    }

    private ResultSerializationProvider buildResultSerializationFactory(Map<?, ?> properties) {
        final ResultSerializationProvider result;
        final Object object;

        if ((object = properties.get(Const.PROPERTY_RESULT_SERIALIZATION_FACTORY)) != null) {
            if (object instanceof ResultSerializationProvider factory) {
                result = factory;
            } else if (object instanceof String string) {
                result = ResultSerializationProvider.get(string);
            } else {
                throw new IllegalArgumentException(
                        String.format(
                                "The property '%s'=%s is incorrect!",
                                Const.PROPERTY_RESULT_SERIALIZATION_FACTORY, object
                        )
                );
            }
        } else {
            result = ResultSerializationProvider.get(io.github.mathter.memifydb.command.v1.Const.ID);
        }

        return result;
    }
}
