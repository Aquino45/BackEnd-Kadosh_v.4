package kado.kadosh.config;

import kado.kadosh.repository.UsuarioRepository;
import kado.kadosh.repository.UsuarioRolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usuarioIdStr) throws UsernameNotFoundException {
        UUID usuarioId = UUID.fromString(usuarioIdStr);

        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + usuarioIdStr));

        List<SimpleGrantedAuthority> authorities = usuarioRolRepository
                .findRolesActivosByUsuarioId(usuarioId)
                .stream()
                .map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.getRol().getNombre()))
                .collect(Collectors.toList());

        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_Cliente"));
        }

        return User.withUsername(usuario.getUsuarioId().toString())
                .password(usuario.getPassword())
                .authorities(authorities)
                .build();
    }
}
